package com.db.jogo.service.impl;

import static com.db.jogo.enums.CartaDoJogoEnumCategoria.*;
import static com.db.jogo.enums.CartaDoJogoEnumTipo.*;
import static com.db.jogo.enums.StatusEnumJogador.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.dto.NovaCategoriaCartasDoJogoDTO;
import com.db.jogo.dto.NovaCategoriaDTO;
import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.JogadorCartasDoJogo;
import com.db.jogo.model.Sala;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.BaralhoService;
import com.db.jogo.service.CartaDoJogoService;
import com.db.jogo.service.JogadorCartasDoJogoService;
import com.db.jogo.service.SalaService;

import org.aspectj.weaver.patterns.OrPointcut;
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
    NovaCategoriaDTO novaCategoriaDTO;
    NovaCategoriaCartasDoJogoDTO novaCategoriaCartasDoJogoDTO;
    JogadorCartasDoJogo jogadorCartasDoJogo;

    private Jogador primeiroJogador;
    private Jogador segundoJogador;
    private CartaDoJogo cartasdoJogoMock;
    private Sala salaMock;
    private List<CartaObjetivo> listaCartasObjetivoMock;

    private CartaObjetivo cartaObjetivoMock;
    private List<CartaDoJogo> listaCartasDoJogoMock;

    @BeforeEach
    public void init() {

        cartaDoJogo = new CartaDoJogo();
        jogadorCartasDoJogo = new JogadorCartasDoJogo();
        novaCategoriaDTO = new NovaCategoriaDTO();

        jogador1 = Jogador.builder()
                .id(UUID.fromString("d5c04bec-d0c0-414a-b160-6383c437267f"))
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
                .id(UUID.fromString("fd7b6723-77e2-4846-bd22-88df15ca150a"))
                .posicao(2)
                .cartasDoJogo(List.of(cartaDoJogo))
                .cartasObjetivo(cartasObjetivo)
                .nome("Teste Jogador 2")
                .pontos(0)
                .coracaoPequeno(2)
                .coracaoGrande(0)
                .bonusCoracaoPequeno(0)
                .bonusCoracaoGrande(0)
                .isHost(false)
                .status(StatusEnumJogador.DEFININDO)
                .build();

        novaCategoriaDTO.setCartaID(UUID.fromString("a3fecf8b-4e47-4c10-9159-0a0450d19fee"));
        novaCategoriaDTO.setNovaCategoria(TEA);
                
        novaCategoriaCartasDoJogoDTO = NovaCategoriaCartasDoJogoDTO.builder()
                .jogadorID(UUID.fromString("d5c04bec-d0c0-414a-b160-6383c437267f"))
                .salaHash("qrGd7sOA")
                .listaDeCartas(List.of(novaCategoriaDTO))
                .build();

        jogadorCartasDoJogo.setJogadorID(UUID.fromString("d5c04bec-d0c0-414a-b160-6383c437267f"));
        jogadorCartasDoJogo.setCartaDoJogoID(UUID.fromString("a3fecf8b-4e47-4c10-9159-0a0450d19fee"));
        jogadorCartasDoJogo.setNovaCategoria(TEA);


        startCartasObjetivoMock();
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

        String hash = "qrGd7sOA";
        primeiroJogador.setStatus(StatusEnumJogador.JOGANDO);
        segundoJogador.setStatus(StatusEnumJogador.JOGANDO);
        primeiroJogador.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
        System.out.println("000  " + primeiroJogador.getCartasDoJogo().get(0).getCategoria());
        segundoJogador.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.GENERICA);
        System.out.println("111  " + segundoJogador.getCartasDoJogo().get(0).getCategoria());
        System.out.println("000  " + primeiroJogador.getCartasDoJogo().get(0).getCategoria());

        List<Jogador> jogadores = new ArrayList<>();
        jogadores.add(primeiroJogador);
        jogadores.add(segundoJogador);

        //jogadores.get(0).getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
        //jogadores.get(1).getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.GENERICA);

        sala.setJogadores(jogadores);

        System.out.println("deve 0 categoria antes: " + sala.getJogadores().get(0).getCartasDoJogo().get(0).getCategoria());
        System.out.println("deve 1 categoria antes: " + sala.getJogadores().get(1).getCartasDoJogo().get(0).getCategoria());

        sala.setStatus(StatusEnum.ULTIMA_RODADA);
        sala.setJogadorEscolhido(sala.getJogadores().get(1));
        sala.setHash(hash);

        webSocketServiceImpl.setIndexDoProximoJogador(sala.getJogadores().get(1).getPosicao());

        when(salaService.findSalaByHash(hash)).thenReturn(Optional.of(sala));

        webSocketServiceImpl.iniciaRodadaDefinicao(sala);

        System.out.println("deve 0: " + sala.getJogadores().get(0).getNome());
        System.out.println("deve 1: " + sala.getJogadores().get(1).getNome());

        System.out.println("deve 0 categoria: " + sala.getJogadores().get(0).getCartasDoJogo().get(0).getCategoria());
        System.out.println("deve 1 categoria: " + sala.getJogadores().get(1).getCartasDoJogo().get(0).getCategoria());

        assertEquals(StatusEnumJogador.FINALIZADO, sala.getJogadores().get(0).getStatus());
        assertEquals(StatusEnumJogador.DEFININDO, sala.getJogadores().get(1).getStatus());
        assertEquals(StatusEnum.AGUARDANDO_DEFINICAO, sala.getStatus());

    }

    @Test
    void deveFinalizarOStatusDoJogadorTeste() throws JogoInvalidoException{
        
        String hash = "qrGd7sOA";        
        when(jogadorService.findById(jogador1.getId())).thenReturn(Optional.of(jogador1));
        when(salaService.findSalaByHash(hash)).thenReturn(Optional.of(sala));
        when(jogadorCartasDoJogoService.findByJogadorIDAndCartaDoJogoID(jogador1.getId(), novaCategoriaDTO.getCartaID())).thenReturn(jogadorCartasDoJogo);

        webSocketServiceImpl.finalizaStatusJogador(novaCategoriaCartasDoJogoDTO);

        verify(jogadorService, times(1)).findById(jogador1.getId());
        verify(salaService, times(1)).findSalaByHash(hash);
        verify(jogadorCartasDoJogoService, times(1)).saveJogadorCartasDoJogo(jogadorCartasDoJogo);
    }

    @Test
    void deveFinalizarOStatusDoJogadorTesteException() throws JogoInvalidoException{

        String hash = "qrGd7sOA";
        when(jogadorService.findById(jogador1.getId())).thenReturn(Optional.of(jogador1));
        when(salaService.findSalaByHash(hash)).thenReturn(null);
        when(jogadorCartasDoJogoService.findByJogadorIDAndCartaDoJogoID(jogador1.getId(), novaCategoriaDTO.getCartaID())).thenReturn(jogadorCartasDoJogo);

        assertThrows(JogoInvalidoException.class, () -> webSocketServiceImpl.finalizaStatusJogador(novaCategoriaCartasDoJogoDTO), "Sala não encontrada");
    }


    // --------------

    private void startCartasObjetivoMock() {
        primeiroJogador = new Jogador(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"),
                1,
                List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                        FILME,
                        FISICA,
                        true,
                        "Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.",
                        2,
                        1,
                        "deficienteonline.com.br",
                        3), new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                        FILME,
                        VISUAL,
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
        segundoJogador = new Jogador(UUID.fromString("af0193df-60e2-49c6-a6b1-c58e7e95a493"),
                2,
                List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                        FILME,
                        TEA,
                        true,
                        "Transtorno do Espectro Autista (TEA) é uma condição do desenvolvimento neurológico, caracterizado por alteração da comunicação, interação social e presença de comportamentos repetitivos e estereotipados.",
                        2,
                        1,
                        "autismo.institutopensi.org.br",
                        3)),
                List.of(new CartaObjetivo(UUID.fromString("870c4cbe-c00e-4533-abe8-af7e9a053681"),
                        "Ganhe 3 pontos se você tiver a maior quantidade de cartas da categoria Transtorno do Espectro Autista (TEA) ao final da partida",
                        3,
                        "TEA",
                        "Sua sobrinha adolescente se identifica com personagens.",
                        5,
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
                FILME,
                FISICA,
                true,
                "Nunca movimente a cadeira de rodas sem antes pedir permissão para a pessoa.",
                2,
                1,
                "deficienteonline.com.br",
                3);

        listaCartasDoJogoMock = List.of(new CartaDoJogo(UUID.fromString("7cbd73e3-fcc8-4d54-8d04-d0ef86a6aef0"),
                FILME,
                FISICA,
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

        sala.setJogadores(List.of(primeiroJogador, segundoJogador));
    }
}


