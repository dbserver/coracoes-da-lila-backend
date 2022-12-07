package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.enums.StatusEnum;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.BaralhoService;
import com.db.jogo.service.CartaDoJogoService;
import com.db.jogo.service.CartaObjetivoService;
import com.db.jogo.service.SalaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private CartaObjetivoService cartaObjetivoService;
    private CartaObjetivo cartaObjetivo = new CartaObjetivo();
    private CartaObjetivo cartaObjetivoNula;
    private Sala sala = new Sala();

    private final WebSocketServiceImpl webSocketServiceImpl = new WebSocketServiceImpl(salaService, baralhoService, jogadorService, template, cartaDoJogoService, cartaObjetivoService);

    @BeforeEach
    public void init(){
        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setDescricao("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setClassificacao("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        sala.setId(UUID.randomUUID());
        sala.setCartasObjetivo(new ArrayList<>());
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);

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
    @DisplayName("Teste do método sorteia carta objetivo, se retorna uma carta contida no arrayList Cartas Objetivo da sala")
    void testSorteiaCartaObjetivo(){
        sala.adicionarCartaDoObjetivo(cartaObjetivo);
        sala.adicionarCartaDoObjetivo(cartaObjetivoNula);
        CartaObjetivo cartaSorteada = webSocketServiceImpl.sorteiaCartaObjetivo(sala);

        assertEquals(sala.cartasObjetivo.contains(cartaSorteada), true);
    }
}
