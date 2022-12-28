package com.db.jogo.service.impl;

import static com.db.jogo.enums.StatusEnumJogador.ESPERANDO;
import static com.db.jogo.enums.StatusEnumJogador.JOGANDO;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.db.jogo.dto.SalaRequest;
import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.model.CartaDoJogo;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;


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

    private Jogador jogadorMock;
    private Jogador jogador2Mock;
    private CartaDoJogo cartasdoJogoMock;
    private Sala salaMock;
    private List<CartaObjetivo> listaCartasObjetivoMock;

    private CartaObjetivo cartaObjetivoMock;
    private List<CartaDoJogo> listaCartasDoJogoMock;

    private final WebSocketServiceImpl webSocketServiceImpl = new WebSocketServiceImpl(salaService, baralhoService, jogadorService, template, cartaDoJogoService);

    @BeforeEach
    public void init(){
        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setTexto_tematico("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setTexto_regra("Ganhe pontos");
        cartaObjetivo.setCategoria("FISICA");
        cartaObjetivo.setTipo("FILME");

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
        jogador.setStatus(ESPERANDO);
        jogador.setIsHost(true);


        jogador2.setId(UUID.randomUUID());
        jogador2.setNome("Guilherme");
        jogador2.setPontos(2);
        jogador2.setBonusCoracaoGrande(3);
        jogador2.setBonusCoracaoPequeno(2);
        jogador2.setCoracaoGrande(5);
        jogador2.setCoracaoPequeno(3);
        jogador2.setStatus(JOGANDO);

        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
        sala.adicionarJogador(jogador2);

        salaRequest.setHash("hashpraentrar");
        salaRequest.setJogador(jogador);


        salaResponse.setSala(sala);
        salaResponse.setJogador(jogador);

        startCartasObjetivoMock();

    }

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
        assertEquals(jogador2.getStatus(), JOGANDO);


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

        assertEquals(jogadorJogando.getStatus(), JOGANDO);
        assertNotEquals(jogadorJogando.getStatus(), StatusEnumJogador.ESPERANDO);
    }


    @Test
    @DisplayName("Testa que o jogador tem alguma carta do tipo igual da categoria da carta objetivo e retorna a soma.")
    void calculaCartasMesmaCategoria() {
        int resultado = webSocketServiceImpl.calculaCartasMesmaCategoria("FISICA", jogadorMock);
        assertEquals(1, resultado);
    }

    @Test
    @DisplayName("Testa que o jogador NÃO tem alguma carta do tipo igual da categoria da carta objetivo e retorna a soma ZERO.")
    void calculaCartasMesmaCategoriaRetornaZero() {
        int resultado = webSocketServiceImpl.calculaCartasMesmaCategoria("FISICA", jogador2Mock);
        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("Testa que jogador tem pelo menos uma carta de tipo igual da carta objetivo")
    void testeVerificaTiposIguais() {
        boolean resultado = webSocketServiceImpl.verificaTiposIguais("FILME", jogadorMock);
        assertTrue(resultado);
    }
    @Test
    @DisplayName("Testa que jogador NÃO TEM tem pelo menos uma carta de tipo igual da carta objetivo")
    void testeVerificaQueNaoTemTiposIguais() {
        boolean resultado = webSocketServiceImpl.verificaTiposIguais("ERRADO", jogadorMock);
        assertFalse(resultado);
    }

    @Test
    void calculaCartasCategoriasDistintasFisica() {
        int resultadoFisica = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(1, resultadoFisica);

        jogadorMock.getCartasObjetivo().get(0).setCategoria("VISUAL");
        jogadorMock.getCartasDoJogo().get(0).setCategoria("VISUAL");
        int resultadoVisual = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(1, resultadoVisual);

        jogadorMock.getCartasObjetivo().get(0).setCategoria("INTELECTUAL");
        jogadorMock.getCartasDoJogo().get(0).setCategoria("INTELECTUAL");
        int resultadoIntelectual = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(1, resultadoIntelectual);

        jogadorMock.getCartasObjetivo().get(0).setCategoria("TEA");
        jogadorMock.getCartasDoJogo().get(0).setCategoria("TEA");
        int resultadoTEA = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(1, resultadoTEA);

        jogadorMock.getCartasObjetivo().get(0).setCategoria("AUDITIVA");
        jogadorMock.getCartasDoJogo().get(0).setCategoria("AUDITIVA");
        int resultadoAuditiva = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(1, resultadoAuditiva);

        jogadorMock.getCartasObjetivo().get(0).setCategoria("GENERICA");
        jogadorMock.getCartasDoJogo().get(0).setCategoria("GENERICA");
        int resultadoGenerica = webSocketServiceImpl.calculaCartasCategoriasDistintasDoJogador(jogadorMock);
        assertEquals(0, resultadoGenerica);

    }

    @Test
    void jogadorTemMaiorVariedadeDeCategorias() {

    }

    @Test
    void logicaContagemTipoCartaObjetivo5() {
    }

    private void startCartasObjetivoMock() {
        jogadorMock = new Jogador(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"),
                1,
                List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                        "FILME",
                        "FISICA",
                        true,
                        "Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.",
                        2,
                        1,
                        "deficienteonline.com.br",
                        3)),
                List.of(new CartaObjetivo(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"),
                        "Ganhe 2 pontos se você tiver alguma carta de filme ao final da partida",
                        2,
                        "FISICA",
                        "Sua sobrinha adolescente se identifica com personagens.",
                        2,
                        "FILME")),
                "Pedro",
                5,
                2,
                1,
                0,
                0,
                true,
                0,
                JOGANDO
        );
        jogador2Mock = new Jogador(UUID.fromString("af0193df-60e2-49c6-a6b1-c58e7e95a493"),
                2,
                List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                        "FILME",
                        "TEA",
                        true,
                        "Transtorno do Espectro Autista (TEA) é uma condição do desenvolvimento neurológico, caracterizado por alteração da comunicação, interação social e presença de comportamentos repetitivos e estereotipados.",
                        2,
                        1,
                        "autismo.institutopensi.org.br",
                        3)),
                List.of(new CartaObjetivo(UUID.fromString("870c4cbe-c00e-4533-abe8-af7e9a053681"),
                        "Ganhe 3 pontos se você tiver a maior quantidade de cartas da categoria Transtorno do Espectro Autista (TEA) ao final da partida",
                        2,
                        "TEA",
                        "Sua sobrinha adolescente se identifica com personagens.",
                        2,
                        "FILME")),
                "Pedro",
                5,
                2,
                1,
                0,
                0,
                false,
                0,
                JOGANDO
        );

        cartasdoJogoMock = new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                "FILME",
                "FISICA",
                true,
                "Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.",
                2,
                1,
                "deficienteonline.com.br",
                3);

        listaCartasDoJogoMock = List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                "FILME",
                "FISICA",
                true,
                "Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.",
                2,
                1,
                "deficienteonline.com.br",
                3));

        cartaObjetivoMock = new CartaObjetivo(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"),
                "Ganhe 2 pontos se você tiver alguma carta de filme ao final da partida",
                2,
                "FISICA",
                "Sua sobrinha adolescente se identifica com personagens.",
                2,
                "FILME");

        listaCartasObjetivoMock = List.of(new CartaObjetivo(UUID.fromString("272f930e-1adc-4405-b4a5-e9b909ce5738"),
                "Ganhe 2 pontos se você tiver alguma carta de filme ao final da partida",
                2,
                "FISICA",
                "Sua sobrinha adolescente se identifica com personagens.",
                2,
                "FILME"));
    }
}
