package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.BaralhoService;
import com.db.jogo.service.CartaDoJogoService;
import com.db.jogo.service.JogadorCartasDoJogoService;
import com.db.jogo.service.SalaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private SalaService salaService;

    @Mock
    private BaralhoService baralhoService;

    @Mock
    private JogadorService jogadorService;

    @Mock
    private CartaDoJogoService cartaDoJogoService;

    @Mock
    private JogadorCartasDoJogoService jogadorCartasDoJogoService;

    @InjectMocks
    private WebSocketServiceImpl webSocketServiceImpl = new WebSocketServiceImpl(salaService, baralhoService, jogadorService, simpMessagingTemplate, cartaDoJogoService, jogadorCartasDoJogoService);

    Sala sala = new Sala();
    Jogador jogador1;
    Jogador jogador2;
    Baralho baralho;
    CartaDoJogo cartaDoJogo;
    CartaObjetivo cartaObjetivo;
    List<Jogador> jogadores;
    List<CartaDoJogo> cartasDoJogo;
    List<CartaObjetivo> cartasObjetivo;

    @BeforeEach
    public void init() {

        cartaDoJogo = new CartaDoJogo();

        jogador1 = Jogador.builder()
                .id(UUID.randomUUID())
                .posicao(1)
                .cartasDoJogo(List.of(cartaDoJogo))
                .cartasObjetivo(cartasObjetivo)
                .nome("Teste Jogador 1")
                .pontos(0)
                .coracaoPequeno(2)
                .coracaoGrande(0)
                .bonusCoracaoPequeno(0)
                .bonusCoracaoGrande(0)
                .isHost(true)
                .status(StatusEnumJogador.DEFININDO)
                .build();

        jogador2 = Jogador.builder()
                .id(UUID.randomUUID())
                .posicao(2)
                .cartasDoJogo(List.of(cartaDoJogo))
                .cartasObjetivo(cartasObjetivo)
                .nome("Teste Jogador 1")
                .pontos(0)
                .coracaoPequeno(2)
                .coracaoGrande(0)
                .bonusCoracaoPequeno(0)
                .bonusCoracaoGrande(0)
                .isHost(true)
                .status(StatusEnumJogador.DEFININDO)
                .build();
    }


    @Test
    void deveVerificarSeJogadorTemCartaGenericaTesteSucesso() {

        cartaDoJogo.setCategoria(CartaDoJogoEnumCategoria.GENERICA);

        boolean temCartaGenerica = webSocketServiceImpl.verificaJogadorTemCartaGenerica(jogador1);

        assertTrue(temCartaGenerica);
    }

    @Test
    void deveVerificarSeJogadorTemCartaGenericaTesteFalha() {

        cartaDoJogo.setCategoria(CartaDoJogoEnumCategoria.VISUAL);

        boolean temCartaGenerica = webSocketServiceImpl.verificaJogadorTemCartaGenerica(jogador1);

        assertFalse(temCartaGenerica);
    }

    @Test
    void deveModificarStatusDoJogadorComoDefinindoTeste(){

        jogador1.setStatus(StatusEnumJogador.JOGANDO);
        cartaDoJogo.setCategoria(CartaDoJogoEnumCategoria.GENERICA);

        webSocketServiceImpl.modificaStatusJogadorDefinindoOuFinalizado(jogador1);

        assertEquals(StatusEnumJogador.DEFININDO, jogador1.getStatus());
    }

    @Test
    void deveModificarStatusDoJogadorComoFinalizadoTeste(){
        jogador1.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
        jogador1.setStatus(StatusEnumJogador.JOGANDO);

        webSocketServiceImpl.modificaStatusJogadorDefinindoOuFinalizado(jogador1);

        assertEquals(StatusEnumJogador.FINALIZADO, jogador1.getStatus());
    }


    @Test
    void deveVerificarSeStatusDoJogadorEstaFinalizadoTeste() {

        jogador1.setStatus(StatusEnumJogador.FINALIZADO);

        boolean temCartaGenerica = webSocketServiceImpl.verificaStatusJogadorFinalizado(jogador1);

        assertTrue(temCartaGenerica);
    }

    @Test
    void deveVerificarSeStatusDoJogadorNaoEstaFinalizadoTeste() {

        jogador1.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
        jogador1.setStatus(StatusEnumJogador.DEFININDO);

        boolean temCartaGenerica = webSocketServiceImpl.verificaStatusJogadorFinalizado(jogador1);

        assertFalse(temCartaGenerica);
    }

    @Test
    void deveAlterarStatusDaSalaParaFinalizadoTeste(){

        sala.setStatus(StatusEnum.JOGANDO);

        webSocketServiceImpl.finalizaJogo(sala);

        assertEquals(StatusEnum.FINALIZADO, sala.getStatus());
    }

    @Test
    void deveVerificarSeStatusDaSalaEstaDefinindoTeste(){

        sala.setStatus(StatusEnum.AGUARDANDO_DEFINICAO);
        sala.setJogadores(List.of(jogador1));
        sala.getJogadores().get(0).setStatus(StatusEnumJogador.FINALIZADO);

        webSocketServiceImpl.modificaStatusSalaDefinindoOuFinalizado(sala);

        assertEquals(StatusEnum.FINALIZADO, sala.getStatus());
    }

    @Test
    void deveVerificarSeStatusDaSalaNaoEstaDefinindoTeste(){

        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(jogador1));

        sala.getJogadores().get(0).setStatus(StatusEnumJogador.DEFININDO);

        webSocketServiceImpl.modificaStatusSalaDefinindoOuFinalizado(sala);

        assertEquals(StatusEnum.AGUARDANDO_DEFINICAO, sala.getStatus());
    }

    @Test
    void deveVerificarSeTodosOsJogadoresDaSalaEstaoFinalizadosTeste(){
        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(jogador1, jogador2));

        sala.getJogadores().get(0).setStatus(StatusEnumJogador.FINALIZADO);
        sala.getJogadores().get(1).setStatus(StatusEnumJogador.FINALIZADO);

        boolean jogadoresFinalizados = webSocketServiceImpl.verificaTodosJogadoresFinalizados(sala);

        assertTrue(jogadoresFinalizados);
    }

    @Test
    void deveVerificarSeTodosOsJogadoresDaSalaNaoEstaoFinalizadosTeste(){
        sala.setStatus(StatusEnum.JOGANDO);
        sala.setJogadores(List.of(jogador1, jogador2));

        sala.getJogadores().get(0).setStatus(StatusEnumJogador.FINALIZADO);
        sala.getJogadores().get(1).setStatus(StatusEnumJogador.DEFININDO);

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

        sala.setJogadores(List.of(jogador1, jogador2));
        sala.setJogadorEscolhido(jogador2);

        webSocketServiceImpl.setIndexDoProximoJogador(jogador2.getPosicao());

        boolean estaNaUltimaJogada = webSocketServiceImpl.verificaUltimaJogadaDoTurno(sala);

        assertTrue(estaNaUltimaJogada);
    }

    @Test
    void deveVerificarSeNaoEstaNaUltimaJogadaDoTurno(){

        sala.setJogadores(List.of(jogador1, jogador2));

        sala.getJogadores().get(0).setPosicao(1);
        sala.getJogadores().get(1).setPosicao(2);

        sala.setJogadorEscolhido(jogador2);

        webSocketServiceImpl.setIndexDoProximoJogador(1);

        boolean estaNaUltimaJogada = webSocketServiceImpl.verificaUltimaJogadaDoTurno(Optional.of(sala).get());

        assertFalse(estaNaUltimaJogada);
    }


    @Test
    void deveIniciarARodadaDefinicaoTeste(){

        sala.setStatus(StatusEnum.ULTIMA_RODADA);
        sala.setJogadores(List.of(jogador1, jogador2));
        sala.setJogadorEscolhido(jogador1);

        jogador1.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
        jogador1.setStatus(StatusEnumJogador.JOGANDO);
        jogador2.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.GENERICA);
        jogador2.setStatus(StatusEnumJogador.JOGANDO);

        webSocketServiceImpl.setIndexDoProximoJogador(jogador1.getPosicao());

        webSocketServiceImpl.iniciaRodadaDefinicao(sala);
    }
}
