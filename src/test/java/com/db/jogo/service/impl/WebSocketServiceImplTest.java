package com.db.jogo.service.impl;

import com.db.jogo.dto.NovaCategoriaCartasDoJogoDTO;
import com.db.jogo.dto.NovaCategoriaDTO;
import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.model.*;
import com.db.jogo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.db.jogo.enums.CartaDoJogoEnumCategoria.*;
import static com.db.jogo.enums.CartaDoJogoEnumTipo.FILME;
import static com.db.jogo.enums.CartaDoJogoEnumTipo.INFORMACAO;
import static com.db.jogo.enums.StatusEnumJogador.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket Service Teste")
class WebSocketServiceImplTest {

    @Mock
    private SalaService salaServiceMock;
    
    @Mock
    private JogadorService jogadorServiceMock;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplateMock;
    @Mock
    private CartaDoJogoService cartaDoJogoServiceMock;
    @Mock
    private JogadorCartasDoJogoService jogadorCartasDoJogoServiceMock;

    @InjectMocks
    private WebSocketServiceImpl webSocketServiceImpl = new WebSocketServiceImpl(salaServiceMock, 
        jogadorServiceMock, 
        simpMessagingTemplateMock, 
        cartaDoJogoServiceMock, 
        jogadorCartasDoJogoServiceMock);
    
    Sala sala = new Sala();
    Jogador primeiroJogador;
    Jogador segundoJogador;
    CartaDoJogo cartaDoJogoAuditiva;
    CartaDoJogo cartaDoJogoFisica;
    CartaDoJogo cartaDoJogoGenerica;
    CartaDoJogo cartaDoJogoIntelectual;
    CartaDoJogo cartaDoJogoTea;
    CartaDoJogo cartaDoJogoVisual;

    List<CartaDoJogo> listaCartasDoJogo;
    CartaObjetivo cartaObjetivoVisual1Ponto;
    CartaObjetivo cartaObjetivo1PontoPorCategoriasDistintas;
    CartaObjetivo cartaObjetivoFilme2Pontos;
    CartaObjetivo cartaObjetivo3PontosPorMaisCategoriasDistintas;
    CartaObjetivo cartaObjetivoTea3PontosPorMaisCategoriasIguais;
    List<CartaObjetivo> listaCartasObjetivo;
    NovaCategoriaDTO novaCategoriaDTO;
    NovaCategoriaCartasDoJogoDTO novaCategoriaCartasDoJogoDTO;
    JogadorCartasDoJogo jogadorCartasDoJogo;
    

    @BeforeEach
    public void init() {

        jogadorCartasDoJogo = new JogadorCartasDoJogo();
        novaCategoriaDTO = new NovaCategoriaDTO();

        novaCategoriaDTO.setCartaID(UUID.fromString("532c5bc9-9516-4bd4-9eb1-9565ec20ee12"));
        novaCategoriaDTO.setNovaCategoria(VISUAL);

        novaCategoriaCartasDoJogoDTO = NovaCategoriaCartasDoJogoDTO.builder()
                .jogadorID(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"))
                .salaHash("qrGd7sOA")
                .listaDeCartas(List.of(novaCategoriaDTO))
                .build();

        jogadorCartasDoJogo.setJogadorID(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"));
        jogadorCartasDoJogo.setCartaDoJogoID(UUID.fromString("a3fecf8b-4e47-4c10-9159-0a0450d19fee"));
        jogadorCartasDoJogo.setNovaCategoria(TEA);

        cartasDoJogoFake();
        cartasObjetivoFake();
        jogadoresFakes();
    }

    @Test
    void retornaQuantidadeDeJogadoresNaSalaPeloHash() {
        String hash = "qrGd7sOA";
        sala.setHash(hash);
        when(salaServiceMock.totalJogadores(hash)).thenReturn(1);

        Integer quantidadeJogadores = webSocketServiceImpl.getQuantidadeJogadores(hash);

        verify(salaServiceMock).totalJogadores(hash);
        assertEquals(1, quantidadeJogadores);
    }
    @Test
    void deveVerificarSeJogadorTemCartaGenericaTesteSucesso() {

        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica));

        boolean temCartaGenerica = webSocketServiceImpl.verificaJogadorTemCartaGenerica(primeiroJogador);

        assertTrue(temCartaGenerica);
    }

    @Test
    void deveVerificarSeJogadorTemCartaGenericaTesteFalha() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual));

        boolean temCartaGenerica = webSocketServiceImpl.verificaJogadorTemCartaGenerica(primeiroJogador);

        assertFalse(temCartaGenerica);
    }

    @Test
    void deveModificarStatusDoJogadorComoDefinindoTeste(){
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica));

        webSocketServiceImpl.modificaStatusJogadorDefinindoOuFinalizado(primeiroJogador);

        assertEquals(StatusEnumJogador.DEFININDO, primeiroJogador.getStatus());
    }

    @Test
    void deveModificarStatusDoJogadorComoFinalizadoTeste(){
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoIntelectual));

        webSocketServiceImpl.modificaStatusJogadorDefinindoOuFinalizado(primeiroJogador);

        assertEquals(StatusEnumJogador.FINALIZADO, primeiroJogador.getStatus());
    }

    @Test
    void deveVerificarSeStatusDoJogadorEstaFinalizadoTeste() {

        primeiroJogador.setStatus(StatusEnumJogador.FINALIZADO);

        boolean statusDoJogadorEstaFinalizado = webSocketServiceImpl.verificaStatusJogadorFinalizado(primeiroJogador);

        assertTrue(statusDoJogadorEstaFinalizado);

    }
    
    @Test
    void deveVerificarSeStatusDoJogadorNaoEstaFinalizadoTeste() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoIntelectual));
        primeiroJogador.setStatus(StatusEnumJogador.DEFININDO);

        boolean statusDoJogadorEstaFinalizado = webSocketServiceImpl.verificaStatusJogadorFinalizado(primeiroJogador);

        assertFalse(statusDoJogadorEstaFinalizado);
    }

    @Test
    void deveAlterarStatusDaSalaParaFinalizadoTeste(){

        sala.setStatus(StatusEnum.JOGANDO);

        webSocketServiceImpl.finalizaJogo(sala);

        assertEquals(StatusEnum.FINALIZADO, sala.getStatus());
    }

    @Test
    void deveVerificarSeStatusDaSalaEstaDefinindoTeste(){
        primeiroJogador.setStatus(FINALIZADO);

        sala.setJogadores(List.of(primeiroJogador));
        sala.setStatus(StatusEnum.AGUARDANDO_DEFINICAO);

        webSocketServiceImpl.modificaStatusSalaDefinindoOuFinalizado(sala);

        assertEquals(StatusEnum.FINALIZADO, sala.getStatus());
    }

    @Test
    void deveVerificarSeStatusDaSalaNaoEstaDefinindoTeste(){
        primeiroJogador.setStatus(DEFININDO);
        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(primeiroJogador));

        webSocketServiceImpl.modificaStatusSalaDefinindoOuFinalizado(sala);

        assertEquals(StatusEnum.AGUARDANDO_DEFINICAO, sala.getStatus());
    }

    @Test
    void deveVerificarSeTodosOsJogadoresDaSalaEstaoFinalizadosTeste(){
        primeiroJogador.setStatus(FINALIZADO);
        segundoJogador.setStatus(FINALIZADO);

        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean jogadoresFinalizados = webSocketServiceImpl.verificaTodosJogadoresFinalizados(sala);

        assertTrue(jogadoresFinalizados);
    }

    @Test
    void deveVerificarSeTodosOsJogadoresDaSalaNaoEstaoFinalizadosTeste(){
        primeiroJogador.setStatus(FINALIZADO);
        segundoJogador.setStatus(DEFININDO);

        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean jogadoresFinalizados = webSocketServiceImpl.verificaTodosJogadoresFinalizados(sala);

        assertFalse(jogadoresFinalizados);
    }

    @Test
    void deveVerificarSeOJogoEstaNaUltimaRodada(){

        sala.setStatus(StatusEnum.ULTIMA_RODADA);

        boolean salaEstaNaUltimaRodada = webSocketServiceImpl.verificaJogoUltimaRodada(Optional.of(sala).get());

        assertTrue(salaEstaNaUltimaRodada);
    }

    @Test
    void deveVerificarSeOJogoNaoEstaNaUltimaRodada(){

        sala.setStatus(StatusEnum.JOGANDO);

        boolean salaEstaNaUltimaRodada = webSocketServiceImpl.verificaJogoUltimaRodada(Optional.of(sala).get());

        assertFalse(salaEstaNaUltimaRodada);
    }

    @Test
    void deveVerificarSeEstaNaUltimaJogadaDoTurno(){

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));
        sala.setJogadorEscolhido(segundoJogador);

        webSocketServiceImpl.setIndexDoProximoJogador(segundoJogador.getPosicao());

        boolean estaNaUltimaJogada = webSocketServiceImpl.verificaUltimaJogadaDoTurno(sala);

        assertTrue(estaNaUltimaJogada);
    }

    @Test
    void deveVerificarSeNaoEstaNaUltimaJogadaDoTurno(){

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        sala.setJogadorEscolhido(segundoJogador);

        webSocketServiceImpl.setIndexDoProximoJogador(1);

        boolean estaNaUltimaJogada = webSocketServiceImpl.verificaUltimaJogadaDoTurno(Optional.of(sala).get());

        assertFalse(estaNaUltimaJogada);
    }


    @Test
    void deveIniciarARodadaDefinicaoTeste() {

        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoIntelectual));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        segundoJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica));
        segundoJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        jogadorCartasDoJogo.setJogadorID(segundoJogador.getId());
        jogadorCartasDoJogo.setCartaDoJogoID(cartaDoJogoGenerica.getId());
        jogadorCartasDoJogo.setNovaCategoria(VISUAL);

        String hash = "qrGd7sOA";
        sala.setHash(hash);
        sala.setJogadores(List.of(primeiroJogador, segundoJogador));
        sala.setStatus(StatusEnum.ULTIMA_RODADA);
        sala.setJogadorEscolhido(sala.getJogadores().get(1));

        webSocketServiceImpl.setIndexDoProximoJogador(sala.getJogadores().get(1).getPosicao());

        when(salaServiceMock.findSalaByHash(hash)).thenReturn(Optional.of(sala));

        webSocketServiceImpl.iniciaRodadaDefinicao(sala);

        assertEquals(FINALIZADO, sala.getJogadores().get(0).getStatus());
        assertEquals(DEFININDO, sala.getJogadores().get(1).getStatus());
        assertEquals(StatusEnum.AGUARDANDO_DEFINICAO, sala.getStatus());
    }

    @Test
    void deveFinalizarOStatusDoJogadorTeste() throws JogoInvalidoException {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        jogadorCartasDoJogo.setJogadorID(primeiroJogador.getId());
        jogadorCartasDoJogo.setCartaDoJogoID(cartaDoJogoGenerica.getId());
        jogadorCartasDoJogo.setNovaCategoria(VISUAL);

        String hash = "qrGd7sOA";
        sala.setHash(hash);
        sala.setJogadores(List.of(primeiroJogador));

        when(jogadorServiceMock.findById(primeiroJogador.getId())).thenReturn(Optional.of(primeiroJogador));
        when(salaServiceMock.findSalaByHash(hash)).thenReturn(Optional.of(sala));
        when(jogadorCartasDoJogoServiceMock
                .findByJogadorIDAndCartaDoJogoID(primeiroJogador.getId(), novaCategoriaDTO.getCartaID()))
                .thenReturn(jogadorCartasDoJogo);

        webSocketServiceImpl.finalizaStatusJogador(novaCategoriaCartasDoJogoDTO);

        verify(jogadorServiceMock, times(1)).findById(primeiroJogador.getId());
        verify(salaServiceMock, atLeast(2)).findSalaByHash(hash);
        verify(jogadorCartasDoJogoServiceMock, times(1)).saveJogadorCartasDoJogo(jogadorCartasDoJogo);
    }

    @Test
    void deveFinalizarOStatusDoJogadorTesteException() throws JogoInvalidoException {

        String hash = "qrGd7sOA";
        when(jogadorServiceMock.findById(primeiroJogador.getId())).thenReturn(Optional.of(primeiroJogador));
        when(salaServiceMock.findSalaByHash(hash)).thenReturn(Optional.empty());  

        assertThrows(JogoInvalidoException.class,
                () -> webSocketServiceImpl.finalizaStatusJogador(novaCategoriaCartasDoJogoDTO), "Sala não encontrada");
    }

    @Test
    void testaContagemPontosObjetivoQuandoJogadorNaoComprouCartasObjetivo() {
        primeiroJogador.setCartasObjetivo(List.of());
        sala.setJogadores(List.of(primeiroJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        int quantidadePontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo();
        boolean pontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo().equals(0);

        assertEquals(0, quantidadePontosObjetivoJogador);
        assertTrue(pontosObjetivoJogador);
    }

    @Test
    void testaContagemPontosObjetivoCase1() {
        when(jogadorServiceMock.saveJogador(primeiroJogador)).thenReturn(primeiroJogador);

        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        sala.setJogadores(List.of(primeiroJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        int quantidadePontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo();
        boolean pontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo().equals(1);

        assertEquals(1, quantidadePontosObjetivoJogador);
        assertTrue(pontosObjetivoJogador);
    }

    @Test
    void testaContagemPontosObjetivoCase2() {
        when(jogadorServiceMock.saveJogador(primeiroJogador)).thenReturn(primeiroJogador);

        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoFilme2Pontos));

        sala.setJogadores(List.of(primeiroJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        boolean pontosObjetivoDoJogado = sala.getJogadores().get(0).getPontosObjetivo().equals(2);

        assertTrue(pontosObjetivoDoJogado);

    }

    @Test
    void testaContagemPontosObjetivoCase3() {
        when(jogadorServiceMock.saveJogador(primeiroJogador)).thenReturn(primeiroJogador);

        primeiroJogador.setCartasDoJogo(listaCartasDoJogo);
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivo1PontoPorCategoriasDistintas));
        sala.setJogadores(List.of(primeiroJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        boolean pontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo().equals(5);
        assertTrue(pontosObjetivoJogador);
    }

    @Test
    void testaContagemPontosObjetivoCase4() {
        when(jogadorServiceMock.saveJogador(primeiroJogador)).thenReturn(primeiroJogador);

        primeiroJogador.setCartasDoJogo(listaCartasDoJogo);
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivo3PontosPorMaisCategoriasDistintas));
        segundoJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        boolean pontosObjetivoJogador = sala.getJogadores().get(0).getPontosObjetivo().equals(3);

        assertTrue(pontosObjetivoJogador);
    }

    @Test
    void testaContagemPontosObjetivoCase5() {

        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoTea, cartaDoJogoTea, cartaDoJogoTea));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoTea3PontosPorMaisCategoriasIguais));
        segundoJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));
        segundoJogador.setCartasObjetivo(List.of(cartaObjetivoTea3PontosPorMaisCategoriasIguais));

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        webSocketServiceImpl.contagemPontosObjetivo(sala);

        boolean quantidadeDePontosObjetivoDoPrimeiroJogadorCase5 = sala.getJogadores().get(0).getPontosObjetivo().equals(3);
        boolean quantidadeDePontosObjetivoDoSegundoJogadorCase5 = sala.getJogadores().get(1).getPontosObjetivo().equals(0);
        assertTrue(quantidadeDePontosObjetivoDoPrimeiroJogadorCase5);
        assertTrue(quantidadeDePontosObjetivoDoSegundoJogadorCase5);
    }

    @Test
    void testaContagemPontosObjetivoChamouIllegalArgumentException() {
        primeiroJogador.setCartasDoJogo(listaCartasDoJogo);
        primeiroJogador.setCartasObjetivo(List.of(new CartaObjetivo(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"), "", 1, "", "", 100, "")));

        sala.setJogadores(List.of(primeiroJogador));

        assertThrows(IllegalArgumentException.class,
                () -> webSocketServiceImpl.contagemPontosObjetivo(sala),
                "\nCategoria da Carta Objetivo não corresponde a nenhuma lógica de contagem\n");

    }

    @Test
    void testaRetornoVerdadeiroQuandoExisteCartaGenerica() {
        Boolean existeCartaGenerica = webSocketServiceImpl.verificaCartaGenerica(cartaDoJogoGenerica);
        assertTrue(existeCartaGenerica);
    }

    @Test
    void testaRetornoFalsoQuandoNãoExisteCartaGenerica() {
        Boolean existeCartaGenerica = webSocketServiceImpl.verificaCartaGenerica(cartaDoJogoFisica);
        assertFalse(existeCartaGenerica);
    }

    @Test
    @DisplayName("Testa que o jogador tem alguma carta do tipo igual da categoria da carta objetivo e retorna a soma.")
    void calculaCartasComMesmaCategoriaQueCartaObjetivoTeste() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual, cartaDoJogoVisual, cartaDoJogoVisual));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));
        String categoriaVisual = cartaObjetivoVisual1Ponto.getCategoria();
        int resultado = webSocketServiceImpl.calculaCartasMesmaCategoria(categoriaVisual, primeiroJogador);
        assertEquals(3, resultado);
    }

    @Test
    void verificaSeExisteGenericaParaCalcularCartasNovaCategoriaQueCartaObjetivoTeste() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        jogadorCartasDoJogo.setJogadorID(primeiroJogador.getId());
        jogadorCartasDoJogo.setCartaDoJogoID(cartaDoJogoGenerica.getId());
        jogadorCartasDoJogo.setNovaCategoria(VISUAL);

        when(jogadorCartasDoJogoServiceMock
                .findByJogadorIDAndCartaDoJogoID(primeiroJogador.getId(), novaCategoriaDTO.getCartaID()))
                .thenReturn(jogadorCartasDoJogo);

        String categoriaVisual = cartaObjetivoVisual1Ponto.getCategoria();
        int resultado = webSocketServiceImpl.calculaCartasMesmaCategoria(categoriaVisual, primeiroJogador);

        assertEquals(1, resultado);
    }

    @Test
    @DisplayName("Testa que o jogador NÃO tem alguma carta do tipo igual da categoria da carta objetivo e retorna a soma ZERO.")
    void calculaCartasMesmaCategoriaRetornaZero() {

        int resultado = webSocketServiceImpl.calculaCartasMesmaCategoria("FISICA", segundoJogador);
        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("Testa que jogador tem pelo menos uma carta de tipo igual da carta objetivo")
    void testeVerificaTiposIguais() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoFilme2Pontos));

        boolean resultado = webSocketServiceImpl.verificaTiposIguais("FILME", primeiroJogador);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Testa que jogador NÃO TEM tem pelo menos uma carta de tipo igual da carta objetivo")
    void testeVerificaQueNaoTemTiposIguais() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoFilme2Pontos));

        boolean resultado = webSocketServiceImpl.verificaTiposIguais("ERRADO", primeiroJogador);
        assertFalse(resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasGenerica() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoGenerica, cartaDoJogoVisual));
        primeiroJogador.setCartasObjetivo(List.of(cartaObjetivoVisual1Ponto));

        jogadorCartasDoJogo.setJogadorID(primeiroJogador.getId());
        jogadorCartasDoJogo.setCartaDoJogoID(cartaDoJogoGenerica.getId());
        jogadorCartasDoJogo.setNovaCategoria(VISUAL);

        when(jogadorCartasDoJogoServiceMock
                .findByJogadorIDAndCartaDoJogoID(primeiroJogador.getId(), novaCategoriaDTO.getCartaID()))
                .thenReturn(jogadorCartasDoJogo);

        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasVisual() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual, cartaDoJogoVisual));
        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasIntelectual() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica, cartaDoJogoFisica));
        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasTea() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoTea, cartaDoJogoTea));
        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasAuditiva() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoAuditiva, cartaDoJogoAuditiva));
        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasFisica() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica, cartaDoJogoFisica));
        int resultado = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(primeiroJogador);
        assertEquals(1, resultado);
    }

    @Test
    void jogadorTemMaiorVariedadeDeCategorias() {
        primeiroJogador.setCartasDoJogo(listaCartasDoJogo);
        segundoJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean primeiroJogadorTemMaiorVariedade = webSocketServiceImpl
                .jogadorTemMaiorVariedadeDeCategorias(sala, primeiroJogador);

        assertTrue(primeiroJogadorTemMaiorVariedade);
    }

    @Test
    void jogadorNaoTemMaiorVariedadeDeCategorias() {
        primeiroJogador.setCartasDoJogo(listaCartasDoJogo);
        segundoJogador.setCartasDoJogo(List.of(cartaDoJogoFisica));

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean segundoJogadorNaoTemMaiorVariedade = webSocketServiceImpl
                .jogadorTemMaiorVariedadeDeCategorias(sala, segundoJogador);

        assertFalse(segundoJogadorNaoTemMaiorVariedade);
    }

    @Test
    void testaQueJogadorDeveTerMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica, cartaDoJogoFisica, cartaDoJogoFisica));
        segundoJogador.setCartasDoJogo(listaCartasDoJogo);

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean primeiroJogadorTemMaisCartasDaCategoria = webSocketServiceImpl
                .jogadorTemMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo("FISICA", primeiroJogador, sala);
        assertTrue(primeiroJogadorTemMaisCartasDaCategoria);
    }

    @Test
    @DisplayName("Testa se o Jogador não tem nenhuma carta do jogo com a categoria igual da carta objetivo")
    void testaQueJogadorNaoTenhaNenhumaCartaDoJogoIgualCartaObjetivo() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoVisual));
        segundoJogador.setCartasDoJogo(listaCartasDoJogo);

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean segundoJogadorNaoTemMaisCartasDaCategoria = webSocketServiceImpl
                .jogadorTemMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo("FISICA", primeiroJogador, sala);
        assertFalse(segundoJogadorNaoTemMaisCartasDaCategoria);
    }

    @Test
    void testaQueJogadorNaoTenhaMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo() {
        primeiroJogador.setCartasDoJogo(List.of(cartaDoJogoFisica, cartaDoJogoFisica, cartaDoJogoFisica));
        segundoJogador.setCartasDoJogo(listaCartasDoJogo);

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));

        boolean segundoJogadorNaoTemMaisCartasDaCategoria = webSocketServiceImpl
                .jogadorTemMaiorQuantidadeDeCategoriasIguaisACategoriaObjetivo("FISICA", segundoJogador, sala);
        assertFalse(segundoJogadorNaoTemMaisCartasDaCategoria);
    }

    private void jogadoresFakes() {
        primeiroJogador = new Jogador();
        primeiroJogador.setId(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"));
        primeiroJogador.setPosicao(1);
        primeiroJogador.setCartasDoJogo(List.of());
        primeiroJogador.setCartasObjetivo(List.of());
        primeiroJogador.setNome("Pedro");
        primeiroJogador.setPontos(0);
        primeiroJogador.setCoracaoPequeno(2);
        primeiroJogador.setCoracaoGrande(0);
        primeiroJogador.setBonusCoracaoGrande(0);
        primeiroJogador.setBonusCoracaoPequeno(0);
        primeiroJogador.setIsHost(true);
        primeiroJogador.setPontosObjetivo(0);
        primeiroJogador.setStatus(JOGANDO);

        segundoJogador = new Jogador();
        segundoJogador.setId(UUID.fromString("00c5506e-d035-4d7d-97d3-ecff0d7f0d58"));
        segundoJogador.setPosicao(2);
        segundoJogador.setCartasDoJogo(List.of());
        segundoJogador.setCartasObjetivo(List.of());
        segundoJogador.setNome("Joao");
        segundoJogador.setPontos(0);
        segundoJogador.setCoracaoPequeno(2);
        segundoJogador.setCoracaoGrande(0);
        segundoJogador.setBonusCoracaoGrande(0);
        segundoJogador.setBonusCoracaoPequeno(0);
        segundoJogador.setIsHost(false);
        segundoJogador.setPontosObjetivo(0);
        segundoJogador.setStatus(JOGANDO);
    }

    private void cartasDoJogoFake() {
        cartaDoJogoAuditiva = new CartaDoJogo();
        cartaDoJogoAuditiva.setId(UUID.fromString("775c7b9f-6ce2-4919-8696-c751dd322ac3"));
        cartaDoJogoAuditiva.setTipo(INFORMACAO);
        cartaDoJogoAuditiva.setCategoria(AUDITIVA);
        cartaDoJogoAuditiva.setBonus(true);
        cartaDoJogoAuditiva.setTexto("Deficiência auditiva é a perda parcial ou total da audição, " +
                "causada por malformação (causa genética) ou lesões no aparelho auditivo.");
        cartaDoJogoAuditiva.setValorCoracaoPequeno(2);
        cartaDoJogoAuditiva.setValorCoracaoPequeno(1);
        cartaDoJogoAuditiva.setFonte("novaescola.org.br");
        cartaDoJogoAuditiva.setPontos(1);

        cartaDoJogoFisica = new CartaDoJogo();
        cartaDoJogoFisica.setId(UUID.fromString("d5adaa3e-d87c-4142-a1b8-db5fd208def4"));
        cartaDoJogoFisica.setTipo(FILME);
        cartaDoJogoFisica.setCategoria(FISICA);
        cartaDoJogoFisica.setBonus(true);
        cartaDoJogoFisica.setTexto("Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.");
        cartaDoJogoFisica.setValorCoracaoPequeno(2);
        cartaDoJogoFisica.setValorCoracaoPequeno(1);
        cartaDoJogoFisica.setFonte("deficienteonline.com.br");
        cartaDoJogoFisica.setPontos(1);

        cartaDoJogoGenerica = new CartaDoJogo();
        cartaDoJogoGenerica.setId(UUID.fromString("532c5bc9-9516-4bd4-9eb1-9565ec20ee12"));
        cartaDoJogoGenerica.setTipo(INFORMACAO);
        cartaDoJogoGenerica.setCategoria(GENERICA);
        cartaDoJogoGenerica.setBonus(false);
        cartaDoJogoGenerica.setTexto("Síndrome é um conjunto de sintomas ou sinais que, " +
                "juntos, evidenciam uma condição particular.");
        cartaDoJogoGenerica.setValorCoracaoPequeno(0);
        cartaDoJogoGenerica.setValorCoracaoPequeno(2);
        cartaDoJogoGenerica.setFonte("novaescola.org.br");
        cartaDoJogoGenerica.setPontos(1);

        cartaDoJogoIntelectual = new CartaDoJogo();
        cartaDoJogoIntelectual.setId(UUID.fromString("3beadf41-2959-4ab4-90cb-9569d0c5c62a"));
        cartaDoJogoIntelectual.setTipo(INFORMACAO);
        cartaDoJogoIntelectual.setCategoria(INTELECTUAL);
        cartaDoJogoIntelectual.setBonus(false);
        cartaDoJogoIntelectual.setTexto("Deficiência intelectual é classificada como um  conjunto de problemas " +
                "que afeta o intelecto de um indivíduo, podendo causar dificuldade de aprendizagem.");
        cartaDoJogoIntelectual.setValorCoracaoPequeno(0);
        cartaDoJogoIntelectual.setValorCoracaoPequeno(2);
        cartaDoJogoIntelectual.setFonte("novaescola.org.br");
        cartaDoJogoIntelectual.setPontos(1);

        cartaDoJogoVisual = new CartaDoJogo();
        cartaDoJogoVisual.setId(UUID.fromString("bb1ebadf-50c8-463c-8eb3-6f3998a466f3"));
        cartaDoJogoVisual.setTipo(FILME);
        cartaDoJogoVisual.setCategoria(VISUAL);
        cartaDoJogoVisual.setBonus(true);
        cartaDoJogoVisual.setTexto("A deficiência visual pode ser congênita ou adquirida ao longo da vida.");
        cartaDoJogoVisual.setValorCoracaoPequeno(2);
        cartaDoJogoVisual.setValorCoracaoPequeno(1);
        cartaDoJogoVisual.setFonte("deficienteonline.com.br");
        cartaDoJogoVisual.setPontos(1);

        cartaDoJogoTea = new CartaDoJogo();
        cartaDoJogoTea.setId(UUID.fromString("f1731854-493f-4d39-8745-39ce48002e98"));
        cartaDoJogoTea.setTipo(FILME);
        cartaDoJogoTea.setCategoria(TEA);
        cartaDoJogoTea.setBonus(true);
        cartaDoJogoTea.setTexto("Transtorno do Espectro Autista (TEA) é uma condição do desenvolvimento neurológico, " +
                "caracterizado por alteração da comunicação, interação social " +
                "e presença de comportamentos repetitivos e estereotipados.");
        cartaDoJogoTea.setValorCoracaoPequeno(2);
        cartaDoJogoTea.setValorCoracaoPequeno(1);
        cartaDoJogoTea.setFonte("deficienteonline.com.br");
        cartaDoJogoTea.setPontos(1);

        listaCartasDoJogo = new ArrayList<>();
        listaCartasDoJogo.add(cartaDoJogoAuditiva);
        listaCartasDoJogo.add(cartaDoJogoFisica);
        listaCartasDoJogo.add(cartaDoJogoIntelectual);
        listaCartasDoJogo.add(cartaDoJogoTea);
        listaCartasDoJogo.add(cartaDoJogoVisual);
    }

    private void cartasObjetivoFake() {
        cartaObjetivoVisual1Ponto = new CartaObjetivo();
        cartaObjetivoVisual1Ponto
                .setId(UUID.fromString("55d5947b-f491-4fe2-8c65-4d1e61ef8561"));
        cartaObjetivoVisual1Ponto.setTextoRegra("Ganhe 1 ponto " +
                "por cada carta que você tiver da categoria Visual ao final da partida");
        cartaObjetivoVisual1Ponto.setPontos(1);
        cartaObjetivoVisual1Ponto.setCategoria("VISUAL");
        cartaObjetivoVisual1Ponto.setTextoTematico("Seu grau de óculos aumentou muito.");
        cartaObjetivoVisual1Ponto.setTipoContagem(1);
        cartaObjetivoVisual1Ponto.setTipo("");

        cartaObjetivoFilme2Pontos = new CartaObjetivo();
        cartaObjetivoFilme2Pontos.setId(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"));
        cartaObjetivoFilme2Pontos.setTextoRegra("Ganhe 2 pontos se você tiver alguma carta de filme ao final da partida");
        cartaObjetivoFilme2Pontos.setPontos(2);
        cartaObjetivoFilme2Pontos.setCategoria("");
        cartaObjetivoFilme2Pontos.setTextoTematico("Sua sobrinha adolescente se identifica com personagens.");
        cartaObjetivoFilme2Pontos.setTipoContagem(2);
        cartaObjetivoFilme2Pontos.setTipo("FILME");

        cartaObjetivo1PontoPorCategoriasDistintas = new CartaObjetivo();
        cartaObjetivo1PontoPorCategoriasDistintas
                .setId(UUID.fromString("c7f83c84-d8b1-454b-bdc4-526cebbd7972"));
        cartaObjetivo1PontoPorCategoriasDistintas
                .setTextoRegra("Ganhe 1 ponto por cada categoria que você tiver ao final da partida");
        cartaObjetivo1PontoPorCategoriasDistintas.setPontos(1);
        cartaObjetivo1PontoPorCategoriasDistintas.setCategoria("");
        cartaObjetivo1PontoPorCategoriasDistintas
                .setTextoTematico("Sua empresa passou a ter mais de 100 funcionários.");
        cartaObjetivo1PontoPorCategoriasDistintas.setTipoContagem(3);
        cartaObjetivo1PontoPorCategoriasDistintas.setTipo("");

        cartaObjetivo3PontosPorMaisCategoriasDistintas = new CartaObjetivo();
        cartaObjetivo3PontosPorMaisCategoriasDistintas
                .setId(UUID.fromString("3c16a975-e904-48f5-9557-816930cdafaf"));
        cartaObjetivo3PontosPorMaisCategoriasDistintas
                .setTextoRegra("Ganhe 3 pontos se você tiver a maior variedade de categorias ao final da partida");
        cartaObjetivo3PontosPorMaisCategoriasDistintas.setPontos(3);
        cartaObjetivo3PontosPorMaisCategoriasDistintas.setCategoria("");
        cartaObjetivo3PontosPorMaisCategoriasDistintas
                .setTextoTematico("Seu filho pequeno está aprendendo a respeitar os coleguinhas.");
        cartaObjetivo3PontosPorMaisCategoriasDistintas.setTipoContagem(4);
        cartaObjetivo3PontosPorMaisCategoriasDistintas.setTipo("");

        cartaObjetivoTea3PontosPorMaisCategoriasIguais = new CartaObjetivo();
        cartaObjetivoTea3PontosPorMaisCategoriasIguais
                .setId(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"));
        cartaObjetivoTea3PontosPorMaisCategoriasIguais
                .setTextoRegra("Ganhe 3 pontos se você tiver a maior quantidade de cartas da categoria " +
                        "Transtorno do Espectro Autista (TEA) ao final da partida");
        cartaObjetivoTea3PontosPorMaisCategoriasIguais.setPontos(3);
        cartaObjetivoTea3PontosPorMaisCategoriasIguais.setCategoria("TEA");
        cartaObjetivoTea3PontosPorMaisCategoriasIguais
                .setTextoTematico("Sua sobrinha adolescente se identifica com personagens.");
        cartaObjetivoTea3PontosPorMaisCategoriasIguais.setTipoContagem(5);
        cartaObjetivoTea3PontosPorMaisCategoriasIguais.setTipo("");

        listaCartasObjetivo = new ArrayList<>();
        listaCartasObjetivo.add(cartaObjetivoVisual1Ponto);
        listaCartasObjetivo.add(cartaObjetivoFilme2Pontos);
        listaCartasObjetivo.add(cartaObjetivo1PontoPorCategoriasDistintas);
        listaCartasObjetivo.add(cartaObjetivo3PontosPorMaisCategoriasDistintas);
        listaCartasObjetivo.add(cartaObjetivoTea3PontosPorMaisCategoriasIguais);
    }
}