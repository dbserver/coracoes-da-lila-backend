package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.UUID;

import com.db.jogo.dto.SalaRequest;
import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.BaralhoService;
import com.db.jogo.service.CartaDoJogoService;
import com.db.jogo.service.SalaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket Service Teste")
class WebSocketServiceImplTest {

    @Mock
    private SalaService salaService;
    private BaralhoService baralhoService;
    private JogadorService jogadorService;
    private SimpMessagingTemplate template;
    private CartaDoJogoService cartaDoJogoService;
    private CartaObjetivo cartaObjetivo = new CartaObjetivo();
    private CartaObjetivo cartaObjetivoNula;
    private Sala sala = new Sala();
    private Jogador jogador = new Jogador();
    private Jogador jogador2 = new Jogador();
    private SalaRequest salaRequest = new SalaRequest();
    private SalaResponse salaResponse = new SalaResponse();

    @InjectMocks
    private final WebSocketServiceImpl webSocketServiceImpl = new WebSocketServiceImpl(salaService,
            baralhoService, jogadorService, template, cartaDoJogoService);

    @BeforeEach
    public void init(){
        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setTextoTematico("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setTextoRegra("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        sala.setId(UUID.randomUUID());
        sala.setCartasObjetivo(new ArrayList<>());
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);

        jogador.setId(UUID.randomUUID());
        jogador.setNome("Felipe");
        jogador.setPontos(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setBonusCoracaoPequeno(0);
        jogador.setCoracaoGrande(0);
        jogador.setCoracaoPequeno(0);
        jogador.setPosicao(1);
        jogador.adicionaObjetivo(cartaObjetivo);
        jogador.setStatus(StatusEnumJogador.JOGANDO);
        jogador.setIsHost(true);


        jogador2.setId(UUID.randomUUID());
        jogador2.setNome("Guilherme");
        jogador2.setPontos(2);
        jogador2.setBonusCoracaoGrande(3);
        jogador2.setBonusCoracaoPequeno(2);
        jogador2.setCoracaoGrande(5);
        jogador2.setCoracaoPequeno(3);

        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
        sala.adicionarJogador(jogador2);
        sala.setJogadorEscolhido(jogador);

        salaRequest.setHash("hashpraentrar");
        salaRequest.setJogador(jogador);


        salaResponse.setSala(sala);
        salaResponse.setJogador(jogador);

    }

    // falta a maior parte dos testes

    @Test
    @DisplayName("Teste do método sorteia carta objetivo, se retorna uma carta contida no arrayList Cartas Objetivo da sala")
    void testSorteiaCartaObjetivo(){
        sala.adicionarCartaDoObjetivo(cartaObjetivo);
        sala.adicionarCartaDoObjetivo(cartaObjetivoNula);
        CartaObjetivo cartaSorteada = webSocketServiceImpl.sorteiaCartaObjetivo(sala);

        assertEquals(sala.cartasObjetivo.contains(cartaSorteada), true);
    }

    @Test
    @DisplayName("Teste para verificar se o jogo está finalizado")
    void testVerificaJogoFinalizado() {

        assertEquals(webSocketServiceImpl.verificaJogoFinalizado(sala), false);
    }

    

   @Test
    @DisplayName("Teste para verificar o método definePosicaoDoProximoJogador")
    void testDefinePosicaoDoProximoJogador() {
        webSocketServiceImpl.definePosicaoDoProximoJogador(sala, jogador);
        assertEquals(webSocketServiceImpl.getIndexDoProximoJogador(), 2);

    }

    @Test
    @DisplayName("Teste para verificar o método passaAVezDoJogador")
    void testPassaAVezDoJogador() {

        webSocketServiceImpl.passaAVezDoJogador(sala);
        assertEquals(jogador2.getStatus(), StatusEnumJogador.JOGANDO);


    }

    @Test
    @DisplayName("Teste do método de validar carta objetivo com a carta existente")
    void testValidaCartaObjetivoTrue(){

        assertEquals(webSocketServiceImpl.validaCartaObjetivo(cartaObjetivo), true);
    }

    @Test
    @DisplayName("Teste do método de validar carta objetivo com a carta inexistente")
    void testValidaCartaObjetivoFalse(){
        
        assertEquals(webSocketServiceImpl.validaCartaObjetivo(cartaObjetivoNula), false);
    }

    @Test
    @DisplayName("Teste para verificar se o jogo está finalizado")
    void testVerificaJogoUltimaRodada() {

        assertEquals(webSocketServiceImpl.verificaJogoUltimaRodada(sala), false);
    }

    @Test
    @DisplayName("Teste para verificar o método verificaUltimaJogadaDoTurno")
    void testVerificaUltimaJogadaDoTurno() {
        
        webSocketServiceImpl.setIndexDoProximoJogador(1);
        assertEquals( webSocketServiceImpl.verificaUltimaJogadaDoTurno(sala), true);
    }

    @Test
    @DisplayName("Teste para verificar o método verificaUltimaJogadaDoTurno False")
    void testVerificaUltimaJogadaDoTurnoFalse() {
        
        webSocketServiceImpl.setIndexDoProximoJogador(2);
        assertEquals( webSocketServiceImpl.verificaUltimaJogadaDoTurno(sala), false);
    }
    
    @Test
    @DisplayName("Teste para verificar o método de atualizar status do jogador para ESPERANDO")
    void testAtualizaStatusDoJogadorEsperando() {
        webSocketServiceImpl.atualizaStatusDoJogadorEsperando(jogador2);
        webSocketServiceImpl.atualizaStatusDoJogadorEsperando(jogador);

        assertEquals(jogador.getStatus(), StatusEnumJogador.ESPERANDO);
        assertEquals(jogador2.getStatus(), StatusEnumJogador.ESPERANDO);
    }

    @Test
    @DisplayName("Teste do método de buscar jogador jogando na sala")
    void testBuscaJogadorJogando() {

        Jogador jogadorJogando = webSocketServiceImpl.buscaJogadorJogando(sala);
        
        assertEquals(jogadorJogando.getStatus(), StatusEnumJogador.JOGANDO);
        assertNotEquals(jogadorJogando.getStatus(), StatusEnumJogador.ESPERANDO);
    }
 
}
