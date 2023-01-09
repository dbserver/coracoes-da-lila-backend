package com.db.jogo.service.impl;

import com.db.jogo.dto.NovaCategoriaCartasDoJogoDTO;
import com.db.jogo.dto.NovaCategoriaDTO;
import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.exception.CartaCompradaInvalidaException;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.exception.JsonInvalidoException;
import com.db.jogo.helper.Dado;
import com.db.jogo.model.*;
import com.db.jogo.service.*;
import com.db.jogo.service.regras.RegrasDoJogo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;
    private SalaService salaService;
    private BaralhoService baralhoService;
    private JogadorService jogadorService;
    private CartaDoJogoService cartaService;
    private Integer indexDoProximoJogador;
    private Jogador jogador;
    private JogadorCartasDoJogoService jogadorCartasDoJogoService;
    private CartaDoJogo cartaComprada;
    private CartaObjetivo cartaCompradaObjetivo;

    protected WebSocketServiceImpl(SalaService salaService, BaralhoService baralhoService,
                                   JogadorService jogadorService,
                                   SimpMessagingTemplate template, CartaDoJogoService cartaService,
                                   JogadorCartasDoJogoService jogadorCartasDoJogoService) {
        this.salaService = salaService;
        this.baralhoService = baralhoService;
        this.jogadorService = jogadorService;
        this.template = template;
        this.cartaService = cartaService;
        this.jogadorCartasDoJogoService = jogadorCartasDoJogoService;
        this.jogador = new Jogador();
        this.cartaComprada = new CartaDoJogo();
        this.cartaCompradaObjetivo = new CartaObjetivo();
    }

    public Optional<Sala> comprarCartaDoJogo(Sala salaFront) throws IllegalArgumentException {

        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        if (verificaJogoFinalizado(salaParaAtualizar.get())) {
            salaParaAtualizar.get().setDado(0);
            return salaParaAtualizar;
        }

        try {
            // verifico se a sala existe no banco
            if (salaParaAtualizar.isPresent()) {
                // AQUI verificar status da sala, se for JOGANDO continua
                for (int index = 0; index < getQuantidadeJogadores(salaParaAtualizar.get().getHash()); index++) {

                    this.jogador = salaParaAtualizar.get().getJogadores().get(index);
                    Jogador jogadorStatusJogandoFront = procuraJogadorJogandoNoFront(salaFront);
                    // verifica qual o jogador da vez
                    if (StatusEnumJogador.JOGANDO.equals(this.jogador.getStatus())) {

                        // Verifica se jogador comprou uma carta
                        if (this.jogador.getCartasDoJogo().size() >= jogadorStatusJogandoFront.getCartasDoJogo()
                                .size()) {
                            this.sendSala(salaParaAtualizar.get());
                            return salaParaAtualizar;
                        }
                        /*---Inicio da Lógica de Comprar Carta----*/

                        // captura qual carta o jogador comprou
                        this.cartaComprada = procuraCartaComprada(salaFront);

                        // verifica se a carta não é nula ou já esta na mão do jogador
                        if (this.cartaComprada.getId() == null) {
                            this.sendSala(salaParaAtualizar.get());
                            return salaParaAtualizar;
                        }
                        if (this.jogador.getCartasDoJogo().contains(cartaComprada)) {
                            this.sendSala(salaParaAtualizar.get());
                            return salaParaAtualizar;
                        }

                        // fazer lógica do jogo e atualizar os status da sala
                        // mapeia o jogador do banco de dados
                        Optional<Jogador> jogadorParaAtualizar = this.jogadorService.findById(this.jogador.getId());

                        // valida se o jogador pode comprar a carta
                        if (RegrasDoJogo.validaCompraCarta(jogadorParaAtualizar.get(), cartaComprada)) {
                            // Seta os pontos da carta no jogador
                            jogadorParaAtualizar.get()
                                    .setPontos(jogadorParaAtualizar.get().getPontos() + cartaComprada.getPontos());

                            // Seta estado da sala para ultima rodada
                            if (jogadorParaAtualizar.get().getPontos() >= 8) {
                                salaParaAtualizar.get().setStatus(StatusEnum.ULTIMA_RODADA);
                                this.salaService.saveSala(salaParaAtualizar.get());
                            }
                            // Retira os corações da carta do jogador
                            this.jogador = RegrasDoJogo.descontaCoracoesCartaDoJogo(this.jogador, cartaComprada);

                            jogadorParaAtualizar.get().setCoracaoGrande(this.jogador.getCoracaoGrande());
                            jogadorParaAtualizar.get().setCoracaoPequeno(this.jogador.getCoracaoPequeno());

                            if (cartaComprada.getBonus()) {
                                // jogador joga o dado
                                Dado dado = new Dado();
                                Jogador jogadorGirouDado = dado.girarDado(this.cartaComprada,
                                        jogadorParaAtualizar.get(),
                                        salaParaAtualizar.get());
                                // jogador é atualizado conforme resultado do dado
                                jogadorParaAtualizar.get()
                                        .setBonusCoracaoGrande(jogadorGirouDado.getBonusCoracaoGrande());
                                jogadorParaAtualizar.get()
                                        .setBonusCoracaoPequeno(jogadorGirouDado.getBonusCoracaoPequeno());
                            } else {
                                salaParaAtualizar.get().setDado(0);
                            }

                            // Salva a carta no jogador
                            Optional<CartaDoJogo> cartaParaAtualizarNoJogador = this.cartaService
                                    .findById(this.cartaComprada.getId());

                            jogadorParaAtualizar.get().adicionaCarta(cartaParaAtualizarNoJogador.get());
                            JogadorCartasDoJogo jogadorCartasDoJogo = new JogadorCartasDoJogo(
                                    jogadorParaAtualizar.get(), cartaParaAtualizarNoJogador.get());
                            this.jogadorCartasDoJogoService.saveJogadorCartasDoJogo(jogadorCartasDoJogo);
                            jogadorParaAtualizar.get().setStatus(StatusEnumJogador.ESPERANDO);

                            this.jogadorService.saveJogador(jogadorParaAtualizar.get());

                            definePosicaoDoProximoJogador(salaParaAtualizar.get(), jogadorParaAtualizar.get());

                            salaParaAtualizar.get().getBaralho().getCartasDoJogo()
                                    .remove(cartaParaAtualizarNoJogador.get());

                            iniciaRodadaDefinicao(salaParaAtualizar.get());
                        }
                    }
                }


                passaAVezDoJogador(salaParaAtualizar.get());

                // TODO: Colocar método para destruir as cartas restantes do jogo
                Optional<Sala> salaRetornoDoSaveNoBanco = Optional.ofNullable(
                        this.salaService.saveSala(salaParaAtualizar.get()));

                // envia a sala para todos os jogadores conectados a sala
                if (salaRetornoDoSaveNoBanco.isPresent()) {
                    this.template.convertAndSend("/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                            salaRetornoDoSaveNoBanco.get());

                    return salaRetornoDoSaveNoBanco;
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Jogada Não pode ser processada!! ", e);
        }

        return salaParaAtualizar;
    }

    public Jogador procuraJogadorJogandoNoFront(Sala sala) {
        for (Jogador jogadorFront : sala.getJogadores()) {
            if (StatusEnumJogador.JOGANDO.equals(jogadorFront.getStatus())) {
                return jogadorFront;
            }
        }
        return null;
    }

    public SalaResponse criarJogo(Jogador jogador) throws JogoInvalidoException {

        if (jogador.getNome().isEmpty()) {
            throw new JogoInvalidoException("dados incorretos");
        }

        Sala sala = new Sala();
        SalaResponse salaResp = new SalaResponse();

        Jogador savedJogador = jogadorService.saveJogador(criarPrimeiroJogador(jogador));
        Baralho baralho = criarBaralho();
        sala.sorteiaCartaInicial(baralho.getCartasInicio());

        Collections.shuffle(baralho.getCartasDoJogo());
        sala.cartasObjetivo = criarCartasObjetivo();
        sala.setId(UUID.randomUUID());
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(savedJogador);
        sala.setHash(sala.generateHash());
        baralho.setCodigo(sala.getHash());
        sala.setBaralho(baralho);
        sala.setDado(0);
        sala.setJogadorEscolhido(jogador);
        salaResp.setJogador(savedJogador);
        sala.setStatus(StatusEnum.AGUARDANDO);
        salaResp.setSala(salaService.saveSala(sala));

        return salaResp;
    }

    // Sortear carta objetivo para ser comprada
    public CartaObjetivo sorteiaCartaObjetivo(Sala sala) {
        CartaObjetivo cartaSorteada;
        Random random = new Random();
        int seletor = random.nextInt(sala.cartasObjetivo.size());
        cartaSorteada = sala.cartasObjetivo.get(seletor);
        return cartaSorteada;
    }

    // Método para verificar se status da sala está como finalizado
    public boolean verificaJogoFinalizado(Sala sala) {
        if (StatusEnum.FINALIZADO.equals(sala.getStatus())) {
            return true;
        }
        return false;
    }

    // Define a posição do próximo jogador
    public void definePosicaoDoProximoJogador(Sala sala, Jogador jogador) {
        if (jogador.getPosicao() >= sala.getJogadores().size()) {
            setIndexDoProximoJogador(1);
        } else {
            setIndexDoProximoJogador(jogador.getPosicao() + 1);
        }
    }

    // Passa o status jogando para o próximo jogador seguindo o
    // indexDoProximoJogador
    public void passaAVezDoJogador(Sala sala) {
        if (StatusEnum.JOGANDO.equals(sala.getStatus()) || StatusEnum.ULTIMA_RODADA.equals(sala.getStatus())) {
            for (Jogador jog : sala.getJogadores()) {
                if (jog.getPosicao() == getIndexDoProximoJogador()
                        && StatusEnumJogador.ESPERANDO.equals(jog.getStatus())) {
                    jog.setStatus(StatusEnumJogador.JOGANDO);
                }
            }
        }
    }

    // Método para validar carta de objetivo
    public boolean validaCartaObjetivo(CartaObjetivo cartaObjetivo) {
        return cartaObjetivo != null;
    }

    // Método para verificar se status da sala é de última rodada
    public boolean verificaJogoUltimaRodada(Sala sala) {
        return StatusEnum.ULTIMA_RODADA.equals(sala.getStatus());
    }

    // Método para verificar se está na última jogada do turno
    public boolean verificaUltimaJogadaDoTurno(Sala sala) {
        for (Jogador jog : sala.getJogadores()) {
            if (jog.getPosicao().equals(sala.getJogadorEscolhido().getPosicao()) && jog.getPosicao().equals(getIndexDoProximoJogador())) {
                return true;
            }
        }
        return false;
    }

    public void finalizaJogo(Sala sala) {
        sala.setStatus(StatusEnum.FINALIZADO);
    }

    // Método para comprar carta de objetivo
    public Optional<Sala> comprarCartaObjetivo(Sala salaFront) {
        // Busca a sala do front recebida de parâmetro pelo hash e atribui à variável
        // salaParaAtualizar
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        // Verifica se esta sala possui status finalizado, e se sim, retorna a sala sem
        // fazer a ação da compra de carta
        if (verificaJogoFinalizado(salaParaAtualizar.get())) {
            salaParaAtualizar.get().setDado(0);
            return salaParaAtualizar;
        }

        try {
            if (salaParaAtualizar.isPresent()) {
                this.jogador = buscaJogadorJogando(salaParaAtualizar.get());

                // Sorteia uma carta da sala atual
                this.cartaCompradaObjetivo = sorteiaCartaObjetivo(salaFront);

                // Verifica se a carta está nula, se sim retorna a sala sem nenhuma ação
                if (!validaCartaObjetivo(this.cartaCompradaObjetivo)) {
                    this.sendSala(salaParaAtualizar.get());
                    return salaParaAtualizar;
                }

                // Verifica se o jogador já possui aquela carta na mão, se sim retorna a sala
                // sem nenhuma ação
                if (this.jogador.getCartasObjetivo().contains(this.cartaCompradaObjetivo)) {
                    this.sendSala(salaParaAtualizar.get());
                    return salaParaAtualizar;
                }

                // --Lógica para atualizar o jogador que comprou a carta
                if (RegrasDoJogo.validaCompraCartaObjetivoCoracaoPequeno(this.jogador)) {

                    this.jogador = RegrasDoJogo.descontaCoracaoPequenoCartaObjetivo(this.jogador);

                    this.jogador.adicionaObjetivo(this.cartaCompradaObjetivo);
                    atualizaStatusDoJogadorEsperando(this.jogador);

                    jogadorService.saveJogador(this.jogador);

                    salaParaAtualizar.get().removerCartaDoObjetivo(this.cartaCompradaObjetivo);

                    definePosicaoDoProximoJogador(salaParaAtualizar.get(), this.jogador);


                    this.salaService.saveSala(salaParaAtualizar.get());
                    iniciaRodadaDefinicao(salaParaAtualizar.get());

                    passaAVezDoJogador(salaParaAtualizar.get());
                }

                // Salva o resultado da compra no banco
                Optional<Sala> salaRetornoDoSaveNoBanco = Optional.ofNullable(
                        this.salaService.saveSala(salaParaAtualizar.get()));

                if (salaRetornoDoSaveNoBanco.isPresent()) {
                    this.template.convertAndSend("/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                            salaRetornoDoSaveNoBanco.get());

                    return salaRetornoDoSaveNoBanco;
                }
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Jogada não pode ser processada!!", e);
        }

        return salaParaAtualizar;
    }

    public void atualizaStatusDoJogadorEsperando(Jogador jogador) {
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
    }

    public Jogador buscaJogadorJogando(Sala sala) {
        Jogador jogador = new Jogador();
        for (int index = 0; index < sala.getJogadores().size(); index++) {
            if (StatusEnumJogador.JOGANDO.equals(sala.getJogadores().get(index).getStatus())) {
                jogador = sala.getJogadores().get(index);
            }
        }
        return jogador;
    }

    public Optional<Sala> escolheEntreDuasCartasObjetivo(Sala salaFront) {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        try {
            salaParaAtualizar.get().opcoesCartaObjetivo.add(sorteiaCartaObjetivo(salaFront));
            salaParaAtualizar.get().opcoesCartaObjetivo.add(sorteiaCartaObjetivo(salaFront));

            while (salaParaAtualizar.get().opcoesCartaObjetivo.get(0) == salaParaAtualizar.get().opcoesCartaObjetivo
                    .get(1)) {
                salaParaAtualizar.get().opcoesCartaObjetivo.set(1, sorteiaCartaObjetivo(salaFront));
            }

            this.jogador = buscaJogadorJogando(salaFront);
            RegrasDoJogo.descontaCoracaoGrande(this.jogador);

            jogadorService.saveJogador(this.jogador);
            salaService.saveSala(salaParaAtualizar.get());

            Optional<Sala> salaRetornoDoSaveNoBanco = Optional.ofNullable(
                    this.salaService.saveSala(salaParaAtualizar.get()));

            if (salaRetornoDoSaveNoBanco.isPresent()) {
                this.template.convertAndSend("/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                        salaRetornoDoSaveNoBanco.get());

                return salaRetornoDoSaveNoBanco;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Jogada não pode ser processada!!", e);
        }
        return salaParaAtualizar;
    }

    public Optional<Sala> compraCartaObjetivoEscolhida(Sala salaFront) {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        // Verifica se esta sala possui status finalizado, e se sim, retorna a sala sem
        // fazer a ação da compra de carta
        if (verificaJogoFinalizado(salaParaAtualizar.get())) {
            salaParaAtualizar.get().setDado(0);
            return salaParaAtualizar;
        }

        try {
            if (salaParaAtualizar.isPresent()) {
                this.jogador = buscaJogadorJogando(salaParaAtualizar.get());

                // Sorteia uma carta da sala atual
                this.cartaCompradaObjetivo = salaFront.cartaObjetivoEscolhida;

                // Verifica se a carta está nula, se sim retorna a sala sem nenhuma ação
                if (validaCartaObjetivo(this.cartaCompradaObjetivo) == false) {
                    this.sendSala(salaParaAtualizar.get());
                    return salaParaAtualizar;
                }

                // Verifica se o jogador já possui aquela carta na mão, se sim retorna a sala
                // sem nenhuma ação
                if (this.jogador.getCartasObjetivo().contains(this.cartaCompradaObjetivo)) {
                    this.sendSala(salaParaAtualizar.get());
                    return salaParaAtualizar;
                }

                // --Lógica para atualizar o jogador que comprou a carta
                this.jogador.adicionaObjetivo(this.cartaCompradaObjetivo);

                atualizaStatusDoJogadorEsperando(this.jogador);

                jogadorService.saveJogador(this.jogador);

                salaParaAtualizar.get().removerCartaDoObjetivo(this.cartaCompradaObjetivo);

                definePosicaoDoProximoJogador(salaParaAtualizar.get(), this.jogador);

                this.salaService.saveSala(salaParaAtualizar.get());

                iniciaRodadaDefinicao(salaParaAtualizar.get());

                passaAVezDoJogador(salaParaAtualizar.get());

                // Salva o resultado da compra no banco
                Optional<Sala> salaRetornoDoSaveNoBanco = Optional.ofNullable(
                        this.salaService.saveSala(salaParaAtualizar.get()));

                if (salaRetornoDoSaveNoBanco.isPresent()) {
                    this.template.convertAndSend(
                            "/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                            salaRetornoDoSaveNoBanco.get());

                    return salaRetornoDoSaveNoBanco;
                }

            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Jogada não pode ser processada!!", e);
        }

        return salaParaAtualizar;

    }

    private CartaDoJogo procuraCartaComprada(Sala sala) throws CartaCompradaInvalidaException {

        CartaDoJogo carta = new CartaDoJogo();

        for (Jogador jogador : sala.getJogadores()) {
            if (jogador.getStatus() == StatusEnumJogador.JOGANDO) {
                try {
                    Integer posicaoCartaComprada = jogador.getCartasDoJogo().size() - 1;
                    if (posicaoCartaComprada >= 0) {
                        carta = jogador.getCartasDoJogo().get(posicaoCartaComprada);
                    }
                    return carta;
                } catch (Exception e) {
                    throw new CartaCompradaInvalidaException("Carta Não encontrada na base de dados");
                }
            }
        }
        return carta;
    }

    public List<CartaObjetivo> criarCartasObjetivo() {
        Baralho baralho = baralhoService.findByCodigo("Clila").get();
        List<CartaObjetivo> cartasObjetivo = baralho.getCartasObjetivo();
        System.out.println(cartasObjetivo);
        return cartasObjetivo;
    }

    private Baralho criarBaralho() {
        Baralho baralho = baralhoService.findByCodigo("Clila").get();
        Baralho baralhoCopy = new Baralho();
        baralhoCopy.setCartasDoJogo(baralho.getCartasDoJogo());
        baralhoCopy.setCartasInicio(baralho.getCartasInicio());
        baralhoCopy.setCodigo("Copy");
        baralhoCopy.setDescricao(baralho.getDescricao());
        baralhoCopy.setTitulo(baralho.getTitulo());
        baralhoCopy.setId(UUID.randomUUID());
        System.out.println(baralhoCopy);
        System.out.println(baralhoCopy.getCartasDoJogo().get(0).getTipo().toString());
        return baralhoService.saveBaralho(baralhoCopy);
    }

    public Jogador criarPrimeiroJogador(Jogador jogador) {
        jogador.setBonusCoracaoPequeno(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(0);
        jogador.setPontos(0);
        jogador.setPosicao(1);
        jogador.setIsHost(true);
        jogador.setNome(jogador.getNome());
        jogador.setPontosObjetivo(0);
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        return jogador;
    }

    public Jogador criarJogador(Jogador jogador, Integer num) {
        jogador.setBonusCoracaoPequeno(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(0);
        jogador.setPontos(0);
        jogador.setPosicao(num);
        jogador.setIsHost(false);
        jogador.setNome(jogador.getNome());
        jogador.setPontosObjetivo(0);
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        return jogador;
    }

    public CartaDoJogo criarCartaDoJogo() {
        CartaDoJogo carta = CartaDoJogo.builder().bonus(false).categoria(null).fonte("").pontos(0).valorCoracaoGrande(0)
                .valorCoracaoPequeno(0).tipo(null).build();
        return carta;
    }

    public Optional<Sala> compraCoracoesPequenos(Sala salaFront) throws IllegalArgumentException {

        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        try {

            if (salaParaAtualizar.isPresent()) {

                for (int index = 0; index < salaParaAtualizar.get().getJogadores().size(); index++) {

                    this.jogador = salaParaAtualizar.get().getJogadores().get(index);

                    if (StatusEnumJogador.JOGANDO.equals(this.jogador.getStatus())) {
                        Jogador jogadorStatusJogandoFront = procuraJogadorJogandoNoFront(salaFront);

                        if (!jogadorStatusJogandoFront.getNome().equals(this.jogador.getNome())) {
                            this.sendSala(salaParaAtualizar.get());
                            return salaParaAtualizar;
                        }

                        Optional<Jogador> jogadorParaAtualizar = this.jogadorService.findById(this.jogador.getId());

                        RegrasDoJogo.adicionaCoracoesPequenos(jogador);

                        jogadorParaAtualizar.get().setCoracaoPequeno(this.jogador.getCoracaoPequeno());

                        jogadorParaAtualizar.get().setStatus(StatusEnumJogador.ESPERANDO);

                        if (jogadorParaAtualizar.get().getPosicao() >= salaParaAtualizar.get().getJogadores()
                                .size()) {
                            this.indexDoProximoJogador = 1;
                        } else {
                            this.indexDoProximoJogador = jogadorParaAtualizar.get().getPosicao() + 1;
                        }

                        this.jogadorService.saveJogador(jogadorParaAtualizar.get());

                        salaParaAtualizar.get().getJogadores().set(index, jogadorParaAtualizar.get());

                    }
                    this.salaService.saveSala(salaParaAtualizar.get());

                    iniciaRodadaDefinicao(salaParaAtualizar.get());
                }
            }

            passaAVezDoJogador(salaParaAtualizar.get());

            salaParaAtualizar.get().setDado(0);

            Optional<Sala> salaRetornoDoSaveNoBanco = Optional
                    .ofNullable(this.salaService.saveSala(salaParaAtualizar.get()));

            if (salaRetornoDoSaveNoBanco.isPresent()) {
                this.template.convertAndSend("/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                        salaRetornoDoSaveNoBanco.get());
                return salaRetornoDoSaveNoBanco;

            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Coração não pode ser comprado!! ", e);
        }
        return salaParaAtualizar;
    }

    // CORAÇÃO GRANDE
    public Optional<Sala> compraCoracoesGrandes(Sala salaFront) throws IllegalArgumentException {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        try {

            if (salaParaAtualizar.isPresent()) {

                for (int index = 0; index < salaParaAtualizar.get().getJogadores().size(); index++) {

                    this.jogador = salaParaAtualizar.get().getJogadores().get(index);

                    if (StatusEnumJogador.JOGANDO.equals(this.jogador.getStatus())) {
                        Jogador jogadorStatusJogandoFront = procuraJogadorJogandoNoFront(salaFront);

                        if (!jogadorStatusJogandoFront.getNome().equals(this.jogador.getNome())) {
                            this.sendSala(salaParaAtualizar.get());
                            return salaParaAtualizar;
                        }
                        Optional<Jogador> jogadorParaAtualizar = this.jogadorService.findById(this.jogador.getId());
                        RegrasDoJogo.adicionaCoracoesGrandes(jogador);

                        jogadorParaAtualizar.get().setCoracaoPequeno(this.jogador.getCoracaoPequeno());

                        jogadorParaAtualizar.get().setStatus(StatusEnumJogador.ESPERANDO);

                        if (jogadorParaAtualizar.get().getPosicao() >= salaParaAtualizar.get().getJogadores()
                                .size()) {
                            this.indexDoProximoJogador = 1;
                        } else {
                            this.indexDoProximoJogador = jogadorParaAtualizar.get().getPosicao() + 1;
                        }

                        this.jogadorService.saveJogador(jogadorParaAtualizar.get());

                        salaParaAtualizar.get().getJogadores().set(index, jogadorParaAtualizar.get());

                    }
                    this.salaService.saveSala(salaParaAtualizar.get());

                    iniciaRodadaDefinicao(salaParaAtualizar.get());

                }
            }

            passaAVezDoJogador(salaParaAtualizar.get());

            salaParaAtualizar.get().setDado(0);

            Optional<Sala> salaRetornoDoSaveNoBanco = Optional
                    .ofNullable(this.salaService.saveSala(salaParaAtualizar.get()));

            if (salaRetornoDoSaveNoBanco.isPresent()) {
                this.template.convertAndSend("/gameplay/game-update/" + salaRetornoDoSaveNoBanco.get().getHash(),
                        salaRetornoDoSaveNoBanco.get());
                return salaRetornoDoSaveNoBanco;

            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Coração não pode ser comprado!! ", e);
        }

        return salaParaAtualizar;

    }

    public SalaResponse conectarJogo(Jogador jogador, String hash) throws JogoInvalidoException {
        if (jogador == null || hash == null) {
            throw new JogoInvalidoException("Parametros nulos");
        }
        Optional<Sala> sala = salaService.findSalaByHash(hash);
        SalaResponse salaResp = new SalaResponse();

        if (sala.isPresent()) {
            if (sala.get().getStatus() == StatusEnum.FINALIZADO) {
                throw new JogoInvalidoException("Jogo ja foi finalizado");
            }
            Jogador savedJogador = jogadorService
                    .saveJogador(criarJogador(jogador, sala.get().getJogadores().size() + 1));
            sala.get().adicionarJogador(savedJogador);

            salaResp.setJogador(savedJogador);
            salaResp.setSala(this.salaService.saveSala(sala.get()));
            salaService.saveSala(sala.get());
        }
        return salaResp;
    }

    public Integer getQuantidadeJogadores(String hash) {

        Integer numero = salaService.totalJogadores(hash);
        String url = "/gameplay/" + hash;
        template.convertAndSend(url, numero);

        return numero;
    }

    public void sendSala(Sala sala) throws JsonInvalidoException {
        ObjectMapper mapper = new ObjectMapper();
        String salaAsJSON;
        String url = "/gameplay/game-update/" + sala.getHash();
        try {
            salaAsJSON = mapper.writeValueAsString(sala);
        } catch (JsonProcessingException e) {
            throw new JsonInvalidoException("Não foi possível construir o JSON da sala.");
        }

        template.convertAndSend(url, salaAsJSON);
    }

    public Optional<Sala> iniciarPartida(Sala sala) throws JogoInvalidoException {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(sala.getHash());
        try {
            if (salaParaAtualizar.isPresent()) {
                salaParaAtualizar.get().setStatus(StatusEnum.JOGANDO);
                salaParaAtualizar.get().setJogadorEscolhido(pegaJogadorEscolhido(sala.getJogadorEscolhido()).get());
                this.salaService.saveSala(salaParaAtualizar.get());

                return salaParaAtualizar;
            }
        } catch (Exception e) {
            throw new JogoInvalidoException("Sala não encontrada");
        }
        return salaParaAtualizar;
    }

    public Integer getIndexDoProximoJogador() {
        return this.indexDoProximoJogador;
    }

    public void setIndexDoProximoJogador(Integer index) {
        this.indexDoProximoJogador = index;
    }

    public Boolean verificaJogadorTemCartaGenerica(Jogador jogador) {
        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            if (jogador.getCartasDoJogo().get(i).getCategoria().equals(CartaDoJogoEnumCategoria.GENERICA)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Jogador> pegaJogadorEscolhido(Jogador jogador) throws JogoInvalidoException {
        Optional<Jogador> atualizarJogador = this.jogadorService.findById(jogador.getId());
        try {
            if (atualizarJogador.isPresent()) {
                atualizarJogador.get().setStatus(StatusEnumJogador.JOGANDO);
                this.jogadorService.saveJogador(atualizarJogador.get());
                return atualizarJogador;
            }
        } catch (Exception e) {
            throw new JogoInvalidoException("Jogador não encontrado");
        }
        return atualizarJogador;
    }

    public void modificaStatusJogadorDefinindoOuFinalizado(Jogador jog) {

        if (verificaJogadorTemCartaGenerica(jog)) {
            jog.setStatus(StatusEnumJogador.DEFININDO);
        } else {
            jog.setStatus(StatusEnumJogador.FINALIZADO);
        }
    }

    public Boolean verificaStatusJogadorFinalizado(Jogador jogador) {
        if (jogador.getStatus().equals(StatusEnumJogador.FINALIZADO)) {
            return true;
        }
        return false;
    }

    public void modificaStatusSalaDefinindoOuFinalizado(Sala sala) {
        if (verificaTodosJogadoresFinalizados(sala)) {
            contagemPontosObjetivo(sala);
            finalizaJogo(sala);
        } else {
            sala.setStatus(StatusEnum.AGUARDANDO_DEFINICAO);
        }
    }

    public Boolean verificaTodosJogadoresFinalizados(Sala sala) {
        int contador = 0;
        for (int i = 0; i < sala.getJogadores().size(); i++) {
            if (verificaStatusJogadorFinalizado(sala.getJogadores().get(i))) {
                contador++;
            }
        }

        boolean jogadoresEstaoFinalizados = contador == sala.getJogadores().size();


        return jogadoresEstaoFinalizados;
    }

    public void iniciaRodadaDefinicao(Sala sala) {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(sala.getHash());
        if (verificaJogoUltimaRodada(salaParaAtualizar.get()) && verificaUltimaJogadaDoTurno(salaParaAtualizar.get())) {
            for (Jogador jogador : sala.getJogadores()) {
                modificaStatusJogadorDefinindoOuFinalizado(jogador);
            }

            this.salaService.saveSala(sala);

            modificaStatusSalaDefinindoOuFinalizado(sala);

            
        }

        if (salaParaAtualizar.isPresent()) {
            this.template.convertAndSend(
                    "/gameplay/game-update/" + salaParaAtualizar.get().getHash(),
                    salaParaAtualizar.get());
        }

    }

    public Sala finalizaStatusJogador(NovaCategoriaCartasDoJogoDTO novaCategoriaCartasDoJogoDTO)
            throws JogoInvalidoException {

        Optional<Jogador> jogadorParaAtualizar = this.jogadorService
                .findById(novaCategoriaCartasDoJogoDTO.getJogadorID());
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(novaCategoriaCartasDoJogoDTO.getSalaHash());

        for (NovaCategoriaDTO novaCategoriaDTO : novaCategoriaCartasDoJogoDTO.getListaDeCartas()) {
            JogadorCartasDoJogo jogadorCartasDoJogo = this.jogadorCartasDoJogoService.findByJogadorIDAndCartaDoJogoID(
                    jogadorParaAtualizar.get().getId(), novaCategoriaDTO.getCartaID());

            jogadorCartasDoJogo.setNovaCategoria(novaCategoriaDTO.getNovaCategoria());
            this.jogadorCartasDoJogoService.saveJogadorCartasDoJogo(jogadorCartasDoJogo);
        }

        try {
            if (salaParaAtualizar.isPresent()) {
                if (jogadorParaAtualizar.isPresent()) {
                    jogadorParaAtualizar.get().setStatus(StatusEnumJogador.FINALIZADO);
                    jogadorService.saveJogador(jogadorParaAtualizar.get());
                }
            }
            modificaStatusSalaDefinindoOuFinalizado(salaParaAtualizar.get());

            if (salaParaAtualizar.isPresent()) {
                this.template.convertAndSend(
                        "/gameplay/game-update/" + salaParaAtualizar.get().getHash(),
                        salaParaAtualizar);

                return salaParaAtualizar.get();
            }

            return salaParaAtualizar.get();

        } catch (Exception e) {
            throw new JogoInvalidoException("Sala não encontrada");
        }
    }

    public void contagemPontosObjetivo(Sala sala) {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(sala.getHash());

        for (Jogador jogador : sala.getJogadores()) {
            boolean jogadorSemCartaObjetivo = jogador.getCartasObjetivo().isEmpty();

            if (jogadorSemCartaObjetivo) {
                jogador.setPontosObjetivo(0);
            } else {
                for (CartaObjetivo cartaObjetivo : jogador.getCartasObjetivo()) {
                    switch (cartaObjetivo.getTipoContagem()) {
                        case 1:
                            int quantidadeCartasMesmaCategoria = calculaCartasMesmaCategoria(cartaObjetivo.getCategoria(), jogador);
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (quantidadeCartasMesmaCategoria * cartaObjetivo.getPontos()));
                            break;
                        case 2:
                            if (verificaTiposIguais(cartaObjetivo.getTipo(), jogador)) {
                                jogador.setPontosObjetivo(jogador.getPontosObjetivo() + cartaObjetivo.getPontos());
                            }
                            break;
                        case 3:
                            int quantidadeCartasCategoriasDistintas = calculaCartasCategoriasDistintasDoJogador(jogador);
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (quantidadeCartasCategoriasDistintas * cartaObjetivo.getPontos()));
                            break;
                        case 4:
                            if (jogadorTemMaiorVariedadeDeCategorias(sala, jogador)) {
                                jogador.setPontosObjetivo(jogador.getPontosObjetivo() + cartaObjetivo.getPontos());
                            }
                            break;
                        case 5:
                            if (jogadorTemMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo(cartaObjetivo.getCategoria(), jogador, sala)) {
                                jogador.setPontosObjetivo(jogador.getPontosObjetivo() + cartaObjetivo.getPontos());
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("\nCategoria da Carta Objetivo não corresponde a nenhuma lógica de contagem\n");
                    }
                }

                this.jogadorService.saveJogador(jogador);
            }
        }
    }

    public Boolean verificaCartaGenerica(CartaDoJogo cartaDoJogo){
        if (cartaDoJogo.getCategoria().equals(CartaDoJogoEnumCategoria.GENERICA)){
            return true;
        }
        return false;
    }

    public Integer calculaCartasMesmaCategoria(String categoria, Jogador jogador) {
        int cartasDeMesmaCategoria = 0;
        boolean categoriasIguais;

        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            CartaDoJogo cartaAtual = jogador.getCartasDoJogo().get(i);

            if (verificaCartaGenerica(cartaAtual)){
                CartaDoJogoEnumCategoria novaCategoria = jogadorCartasDoJogoService
                        .findByJogadorIDAndCartaDoJogoID(jogador.getId(), cartaAtual.getId()).getNovaCategoria();
                categoriasIguais = novaCategoria.toString().equals(categoria);
            }else{
                categoriasIguais = cartaAtual.getCategoria().toString().equals(categoria);
            }

            if (categoriasIguais)
                cartasDeMesmaCategoria++;
        }

        return cartasDeMesmaCategoria;
    }

    public Boolean verificaTiposIguais(String tipo, Jogador jogador) {
        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            boolean tiposIguais = jogador.getCartasDoJogo().get(i).getTipo().toString().equals(tipo);
            if (tiposIguais)
                return true;
        }
        return false;
    }

    public Integer calculaCartasCategoriasDistintasDoJogador(Jogador jogador) {

        Integer[] contadorDeCategorias = {0, 0, 0, 0, 0};

        int cartasDeCategoriasDistintas;
        CartaDoJogoEnumCategoria categoria;

        for (CartaDoJogo cartaDoJogo : jogador.getCartasDoJogo()) {
            if(verificaCartaGenerica(cartaDoJogo)){
                categoria = jogadorCartasDoJogoService.findByJogadorIDAndCartaDoJogoID(jogador.getId(), cartaDoJogo.getId()).getNovaCategoria();
            }else {
                categoria = cartaDoJogo.getCategoria();
            }

            switch (categoria) {
                case VISUAL -> contadorDeCategorias[0]++;
                case INTELECTUAL -> contadorDeCategorias[1]++;
                case TEA -> contadorDeCategorias[2]++;
                case AUDITIVA -> contadorDeCategorias[3]++;
                case FISICA -> contadorDeCategorias[4]++;
            }
        }

        cartasDeCategoriasDistintas = (int) Arrays.stream(contadorDeCategorias)
                .filter(contadorDeCategoria -> contadorDeCategoria != 0).count();

        return cartasDeCategoriasDistintas;
    }

    public Boolean jogadorTemMaiorVariedadeDeCategorias(Sala sala, Jogador jogador) {

        int quantidadeCategoriasDistintasJogadorAtual = calculaCartasCategoriasDistintasDoJogador(jogador);
        int quantidadeCategoriasDistintasAdversario;

        for (Jogador jogadorAdversario : sala.getJogadores()) {

            if (jogadorAdversario.getId() != jogador.getId()) {

                quantidadeCategoriasDistintasAdversario = calculaCartasCategoriasDistintasDoJogador(jogadorAdversario);

                if (quantidadeCategoriasDistintasAdversario >= quantidadeCategoriasDistintasJogadorAtual)
                    return false;
            }
        }

        return true;
    }

    public Boolean jogadorTemMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo(String categoriaObjetivo, Jogador jogador, Sala sala) {

        int quantidadeCategoriasIguaisDoJogadorAtual = calculaCartasMesmaCategoria(categoriaObjetivo, jogador);
        int quantidadeCategoriasIguaisAdversario;

        if (quantidadeCategoriasIguaisDoJogadorAtual == 0)
            return false;

        for (Jogador jogadorAdversario : sala.getJogadores()) {

            if (jogadorAdversario.getId() != jogador.getId()) {

                quantidadeCategoriasIguaisAdversario = calculaCartasMesmaCategoria(categoriaObjetivo, jogadorAdversario);

                if (quantidadeCategoriasIguaisAdversario >= quantidadeCategoriasIguaisDoJogadorAtual)
                    return false;
            }
        }

        return true;
    }
}