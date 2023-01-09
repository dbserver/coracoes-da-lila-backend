package com.db.jogo.service.impl;

import static com.db.jogo.enums.CartaDoJogoEnumCategoria.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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

    // private Jogador primeiroJogador;
    // private Jogador segundoJogador;
    // private CartaDoJogo cartasdoJogoMock;
    // private Sala salaMock;
    // private List<CartaObjetivo> listaCartasObjetivoMock;

    // private CartaObjetivo cartaObjetivoMock;
    // private List<CartaDoJogo> listaCartasDoJogoMock;

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


        // startCartasObjetivoMock();
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


    // @Test
    // void deveIniciarARodadaDefinicaoTeste(){

    //     String hash = "qrGd7sOA";
    //     // primeiroJogador.setStatus(StatusEnumJogador.JOGANDO);
    //     // segundoJogador.setStatus(StatusEnumJogador.JOGANDO);
    //     // primeiroJogador.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
    //     // System.out.println("000  " + primeiroJogador.getCartasDoJogo().get(0).getCategoria());
    //     // segundoJogador.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.GENERICA);
    //     // System.out.println("111  " + segundoJogador.getCartasDoJogo().get(0).getCategoria());
    //     // System.out.println("000  " + primeiroJogador.getCartasDoJogo().get(0).getCategoria());
        
    //     jogador1.setStatus(StatusEnumJogador.JOGANDO);
    //     jogador2.setStatus(StatusEnumJogador.JOGANDO);
    //     jogador1.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.INTELECTUAL);
    //     System.out.println("jogador1  " + jogador1.getCartasDoJogo().get(0).getCategoria());
    //     jogador2.getCartasDoJogo().get(0).setCategoria(CartaDoJogoEnumCategoria.GENERICA);

    //     System.out.println("jogador2  " + jogador2.getCartasDoJogo().get(0).getCategoria());
    //     System.out.println("jogador1  " + jogador1.getCartasDoJogo().get(0).getCategoria());        

    //     List<Jogador> jogadores = new ArrayList<>();
    //     jogadores.add(jogador1);
    //     jogadores.add(jogador2);


    //     sala.setJogadores(jogadores);

    //     sala.setStatus(StatusEnum.ULTIMA_RODADA);
    //     sala.setJogadorEscolhido(sala.getJogadores().get(1));
    //     sala.setHash(hash);

    //     webSocketServiceImpl.setIndexDoProximoJogador(sala.getJogadores().get(1).getPosicao());

    //     when(salaService.findSalaByHash(hash)).thenReturn(Optional.of(sala));

    //     webSocketServiceImpl.iniciaRodadaDefinicao(sala);

    //     assertEquals(StatusEnumJogador.FINALIZADO, sala.getJogadores().get(0).getStatus());
    //     assertEquals(StatusEnumJogador.DEFININDO, sala.getJogadores().get(1).getStatus());
    //     assertEquals(StatusEnum.AGUARDANDO_DEFINICAO, sala.getStatus());
    // }

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
    
}


