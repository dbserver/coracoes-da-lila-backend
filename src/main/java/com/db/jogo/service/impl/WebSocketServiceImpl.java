package com.db.jogo.service.impl;

import java.lang.reflect.Array;
import java.util.*;

import com.db.jogo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.exception.CartaCompradaInvalidaException;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.exception.JsonInvalidoException;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.helper.Dado;
import com.db.jogo.service.regras.RegrasDoJogo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private SimpMessagingTemplate template;
    private SalaService salaService;
    private BaralhoService baralhoService;
    private JogadorService jogadorService;
    private CartaDoJogoService cartaService;
    private Integer indexDoProximoJogador;
    private Jogador jogador;
    private CartaDoJogo cartaComprada;
    private CartaObjetivo cartaCompradaObjetivo;

    @Autowired
    protected WebSocketServiceImpl(SalaService salaService, BaralhoService baralhoService,
                                   JogadorService jogadorService,
                                   SimpMessagingTemplate template, CartaDoJogoService cartaService) {
        this.salaService = salaService;
        this.baralhoService = baralhoService;
        this.jogadorService = jogadorService;
        this.template = template;
        this.cartaService = cartaService;
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
                            jogadorParaAtualizar.get().setStatus(StatusEnumJogador.ESPERANDO);

                            this.jogadorService.saveJogador(jogadorParaAtualizar.get());

                            definePosicaoDoProximoJogador(salaParaAtualizar.get(), jogadorParaAtualizar.get());

                            salaParaAtualizar.get().getBaralho().getCartasDoJogo()
                                    .remove(cartaParaAtualizarNoJogador.get());

                            // Verifica se o próximo jogador é o que iniciou a partida e encerra a partida
                            iniciaRodadaDefinicao(salaFront);
							/* if (StatusEnum.ULTIMA_RODADA.equals(salaParaAtualizar.get().getStatus())) {

								for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
									modificaStatusJogador(jog);
		
									if (jog.getStatus().equals(StatusEnumJogador.FINALIZADO)) {
										if (jog.getPosicao() == this.indexDoProximoJogador && jog.getIsHost()) {
											salaParaAtualizar.get().setStatus(StatusEnum.FINALIZADO);
											break;
										}
									}
								}
							} */
                        }
                        /*---*Fim da Lógica para Adicionar a Carta*----*/
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
        baralho.sorteiaCartaInicial();
        Collections.shuffle(baralho.getCartasDoJogo());
        Collections.shuffle(baralho.getCartasInicio());
        sala.cartasObjetivo = criarCartasObjetivo();

        sala.setId(UUID.randomUUID());
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(savedJogador);
        sala.setHash(sala.generateHash());
        baralho.setCodigo(sala.getHash());
        sala.setBaralho(baralho);
        sala.setDado(0);
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
        for (Jogador jog : sala.getJogadores()) {
            if (StatusEnum.JOGANDO.equals(sala.getStatus()) || StatusEnum.ULTIMA_RODADA.equals(sala.getStatus())) {
                if (jog.getPosicao() == getIndexDoProximoJogador()) {
                    jog.setStatus(StatusEnumJogador.JOGANDO);
                }
            }
        }
    }

    // Método para validar carta de objetivo
    public boolean validaCartaObjetivo(CartaObjetivo cartaObjetivo) {
        if (cartaObjetivo == null) {
            return false;
        }
        return true;
    }

    // Método para verificar se status da sala é de última rodada
    public boolean verificaJogoUltimaRodada(Sala sala) {
        if (StatusEnum.ULTIMA_RODADA.equals(sala.getStatus())) {
            return true;
        }
        return false;
    }

    // Método para verificar se está na última jogada do turno
    public boolean verificaUltimaJogadaDoTurno(Sala sala) {
        for (Jogador jog : sala.getJogadores()) {
            if (jog.getPosicao() == getIndexDoProximoJogador() && jog.getIsHost()) {
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
                if (RegrasDoJogo.validaCompraCartaObjetivoCoracaoPequeno(this.jogador)) {

                    this.jogador = RegrasDoJogo.descontaCoracaoPequenoCartaObjetivo(this.jogador);

                    this.jogador.adicionaObjetivo(this.cartaCompradaObjetivo);
                    atualizaStatusDoJogadorEsperando(this.jogador);

                    jogadorService.saveJogador(this.jogador);

                    salaParaAtualizar.get().removerCartaDoObjetivo(this.cartaCompradaObjetivo);

                    definePosicaoDoProximoJogador(salaParaAtualizar.get(), this.jogador);

                    if (verificaJogoUltimaRodada(salaParaAtualizar.get())) {
                        if (verificaUltimaJogadaDoTurno(salaParaAtualizar.get())) {
                            finalizaJogo(salaParaAtualizar.get());
                        }
                    }

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

    // ------US073

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

                if (verificaJogoUltimaRodada(salaParaAtualizar.get())) {
                    if (verificaUltimaJogadaDoTurno(salaParaAtualizar.get())) {
                        finalizaJogo(salaParaAtualizar.get());
                    }
                }

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

        } catch (

                Exception e) {
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
        jogador.setStatus(StatusEnumJogador.JOGANDO);
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
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        return jogador;
    }

    public CartaDoJogo criarCartaDoJogo() {
        CartaDoJogo carta = CartaDoJogo.builder().bonus(false).categoria("").fonte("").pontos(0).valorCoracaoGrande(0)
                .valorCoracaoPequeno(0).tipo("").build();
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

                    iniciaRodadaDefinicao(salaFront);


					/* if (StatusEnum.ULTIMA_RODADA.equals(salaParaAtualizar.get().getStatus())) {

						for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
							modificaStatusJogador(jog);

							if (jog.getStatus().equals(StatusEnumJogador.FINALIZADO)) {
								if (jog.getPosicao() == this.indexDoProximoJogador && jog.getIsHost()) {
									salaParaAtualizar.get().setStatus(StatusEnum.FINALIZADO);
									break;
								}
							}
						}
					} */
                }
            }

            for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
                if (jog.getPosicao() == this.indexDoProximoJogador) {
                    jog.setStatus(StatusEnumJogador.JOGANDO);
                }
            }

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

                    iniciaRodadaDefinicao(salaFront);
					/* if (StatusEnum.ULTIMA_RODADA.equals(salaParaAtualizar.get().getStatus())) {

						for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
							modificaStatusJogador(jog);

							if (jog.getStatus().equals(StatusEnumJogador.FINALIZADO)) {
								if (jog.getPosicao() == this.indexDoProximoJogador && jog.getIsHost()) {
									salaParaAtualizar.get().setStatus(StatusEnum.FINALIZADO);
									break;
								}
							}
						}
					} */
                }
            }

            for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
                if (jog.getPosicao() == this.indexDoProximoJogador) {
                    jog.setStatus(StatusEnumJogador.JOGANDO);
                }
            }

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

    public void modificaStatusJogador(Jogador jog) {
        boolean achouGenerica = false;
        for (int i = 0; i < jog.getCartasDoJogo().size(); i++) {
            if (jog.getCartasDoJogo().get(i).getCategoria().equals("Genérica")) {
                jog.setStatus(StatusEnumJogador.DEFININDO);
                achouGenerica = true;
            }
        }
        if (!achouGenerica) {
            jog.setStatus(StatusEnumJogador.FINALIZADO);
        }
    }

    public void iniciaRodadaDefinicao(Sala salaFront) {
        Optional<Sala> salaParaAtualizar = this.salaService.findSalaByHash(salaFront.getHash());

        if (verificaJogoUltimaRodada(salaFront) && verificaUltimaJogadaDoTurno(salaFront)) {
            for (Jogador jog : salaParaAtualizar.get().getJogadores()) {
                modificaStatusJogador(jog);
            }

            salaParaAtualizar.get().setStatus(StatusEnum.AGUARDANDO_DEFINICAO);
            this.salaService.saveSala(salaParaAtualizar.get());
        }
    }

    public void contagemPontosObjetivo(Sala sala) {
        for (Jogador jogador : sala.getJogadores()) {
            boolean jogadorSemCartaObjetivo = jogador.getCartasObjetivo().isEmpty();
            if (jogadorSemCartaObjetivo) {
                jogador.setPontosObjetivo(0);
            } else {
                for (CartaObjetivo cartaObjetivo : jogador.getCartasObjetivo()) {
                    switch (cartaObjetivo.getTipo_contagem()) {
                        case 1:
                            int resultado1 = logicaContagemTipoCartaObjetivo1(cartaObjetivo.getCategoria());
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (resultado1 * cartaObjetivo.getPontos()));
                            break;
                        case 2:
                            int resultado2 = logicaContagemTipoCartaObjetivo2(cartaObjetivo.getTipo());
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (resultado2 * cartaObjetivo.getPontos()));
                            break;
                        case 3:
                            int resultado3 = logicaContagemTipoCartaObjetivo3();
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (resultado3 * cartaObjetivo.getPontos()));
                            break;
                        case 4:
                            int resultado4 = logicaContagemTipoCartaObjetivo4(jogador.getId(), sala);
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (resultado4 * cartaObjetivo.getPontos()));
                            break;
                        case 5:
                            int resultado5 = logicaContagemTipoCartaObjetivo5(cartaObjetivo.getCategoria(), jogador.getId(), sala);
                            jogador.setPontosObjetivo(jogador.getPontosObjetivo() + (resultado5 * cartaObjetivo.getPontos()));
                            break;
                        default:
                            throw new RuntimeException("\nCategoria da Carta Objetivo não corresponde a nenhuma lógica de contagem\n");
                    }
                }
            }
        }
    }

    public Integer logicaContagemTipoCartaObjetivo1(String categoria) {
        int cartasDeMesmaCategoria = 0;

        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            boolean categoriasIguais = jogador.getCartasDoJogo().get(i).getCategoria().equals(categoria);

            if (categoriasIguais)
                cartasDeMesmaCategoria++;
        }
        return cartasDeMesmaCategoria;
    }

    public Integer logicaContagemTipoCartaObjetivo2(String tipo) {
        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            boolean tiposIguais = jogador.getCartasDoJogo().get(i).getTipo().equals(tipo);

            if (tiposIguais)
                return 1;
        }

        return 0;
    }

    public Integer logicaContagemTipoCartaObjetivo3() {
        Integer[] contadorDeCategorias = {0, 0, 0, 0, 0};
        Integer cartasDeCategoriasDistintas = 0;
        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            switch (jogador.getCartasDoJogo().get(i).getCategoria()) {
                case "VISUAL" -> contadorDeCategorias[0]++;
                case "INTELECTUAL" -> contadorDeCategorias[1]++;
                case "TEA" -> contadorDeCategorias[2]++;
                case "AUDITIVA" -> contadorDeCategorias[3]++;
                default -> //para acrescentar na física
                        contadorDeCategorias[4]++;
            }
        }
        for (Integer contadorDeCategoria : contadorDeCategorias) {
            if (contadorDeCategoria != 0) {
                cartasDeCategoriasDistintas++;
            }
        }

        return cartasDeCategoriasDistintas;
    }

    public Integer logicaContagemTipoCartaObjetivo4(UUID idDoJogadorComCartaObjetivo, Sala sala) {
        Integer[] categoriasDistintas = {0, 0, 0, 0, 0};
        Integer categoriasDistintasJogador = 0;
        int aux = 0;

        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            String categoriaCartaDoJogador = jogador.getCartasDoJogo().get(i).getCategoria();

            switch (categoriaCartaDoJogador) {
                case "VISUAL" -> categoriasDistintas[0]++;
                case "INTELECTUAL" -> categoriasDistintas[1]++;
                case "TEA" -> categoriasDistintas[2]++;
                case "AUDITIVA" -> categoriasDistintas[3]++;
                default -> //para acrescentar na física
                        categoriasDistintas[4]++;
            }
        }
        for (Integer contadorDeCategoria : categoriasDistintas) {
            if (contadorDeCategoria != 0) {
                aux++;
            }
        }
        categoriasDistintasJogador = aux;


        for (Jogador jogador : sala.getJogadores()) {
            Integer[] categoriasDistintasAdversario = {0, 0, 0, 0, 0};
            aux = 0;
            Integer categoriasAdversario = 0;
            for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
                boolean forUmAdversario = sala.getJogadores().get(i).getId() != idDoJogadorComCartaObjetivo;

                if (forUmAdversario) {
                    for (int j = 0; j < jogador.getCartasDoJogo().size(); j++) {
                        String categoriaCartaDoAdversario = jogador.getCartasDoJogo().get(j).getCategoria();

                        switch (categoriaCartaDoAdversario) {
                            case "VISUAL" -> categoriasDistintasAdversario[0]++;
                            case "INTELECTUAL" -> categoriasDistintasAdversario[1]++;
                            case "TEA" -> categoriasDistintasAdversario[2]++;
                            case "AUDITIVA" -> categoriasDistintasAdversario[3]++;
                            default -> //para acrescentar na física
                                    categoriasDistintasAdversario[4]++;
                        }
                    }

                    for (Integer contadorDeCategoria : categoriasDistintasAdversario) {
                        if (contadorDeCategoria != 0) {
                            aux++;
                        }
                    }
                    categoriasAdversario = aux;

                    if (categoriasAdversario > categoriasDistintasJogador)
                        return 0;
                }
            }
        }

        // Se nenhum jogador adversário tem mais cartas de categorias distintas
        return 1;
    }

    public Integer logicaContagemTipoCartaObjetivo5(String categoria, UUID idDoJogadorComCartaObjetivo, Sala sala) {

        int cartasIguaisDoJogador = 0;
        int aux = 0;
        for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
            boolean categoriasIguais = jogador.getCartasDoJogo().get(i).getCategoria().equals(categoria);

            if (categoriasIguais)
                aux++;
        }
        cartasIguaisDoJogador = aux;

        for (Jogador jogador : sala.getJogadores()) {
            int cartasIguaisDoAdversario = 0;
            for (int i = 0; i < jogador.getCartasDoJogo().size(); i++) {
                boolean forUmAdversario = sala.getJogadores().get(i).getId() != idDoJogadorComCartaObjetivo;

                if (forUmAdversario) {
                    if (jogador.getCartasDoJogo().get(i).getCategoria().equals(categoria))
                        cartasIguaisDoAdversario++;
                }
                if (cartasIguaisDoAdversario > cartasIguaisDoJogador)
                    return 0;
            }
        }

        // Se nenhum jogador adversário tem mais cartas de mesma categoria
        return 1;
    }
}