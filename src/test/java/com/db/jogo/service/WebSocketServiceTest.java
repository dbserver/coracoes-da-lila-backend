package com.db.jogo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.db.jogo.service.impl.WebSocketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.db.jogo.dto.SalaRequest;
import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaInicio;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;

@ExtendWith(MockitoExtension.class)
public class WebSocketServiceTest {

    @Mock
    WebSocketServiceImpl webSocketServiceImpl;
    CartaInicio cartaInicio = new CartaInicio();
    Baralho baralho = new Baralho();
    CartaDoJogo carta = new CartaDoJogo();
    CartaObjetivo cartaObjetivo = new CartaObjetivo();
    List <CartaObjetivo> cartasObjetivo = new ArrayList<>();
    Jogador jogador = new Jogador();
    Jogador jogador2 = new Jogador();
    Sala sala = new Sala();
    SalaRequest salaRequest = new SalaRequest();
    SalaResponse salaResponse = new SalaResponse();
    Integer numero = 0;

    @BeforeEach
    public void init(){
        cartaInicio.setId(UUID.randomUUID());
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");

        carta.setId(UUID.randomUUID());
        carta.setPontos(2);
        carta.setBonus(true);
        carta.setCategoria("Visual");
        carta.setTexto("Deficiencia visual");
        carta.setFonte("Wikipedia");
        carta.setValorCoracaoGrande(0);
        carta.setValorCoracaoPequeno(0);
        carta.setTipo(CartaDoJogoEnumTipo.ACAO);

        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setTexto_tematico("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setTexto_regra("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        baralho.setCodigo("qwerty");
        baralho.setId(UUID.randomUUID());
        baralho.setTitulo("Teste");
        baralho.setDescricao("Exemplo");
        baralho.setCartasInicio(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);
        baralho.setCartasDoJogo(new ArrayList<>());
        baralho.adicionarCartadoJogo(carta);
        baralho.setCartasObjetivo(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);

        jogador.setId(UUID.randomUUID());
        jogador.setNome("Felipe");
        jogador.setPontos(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setBonusCoracaoPequeno(0);
        jogador.setCoracaoGrande(0);
        jogador.setCoracaoPequeno(0);
        jogador.setPosicao(2);
        jogador.adicionaCarta(carta);
        jogador.adicionaObjetivo(cartaObjetivo);

        jogador2.setId(UUID.randomUUID());
        jogador2.setNome("Guilherme");
        jogador2.setPontos(2);
        jogador2.setBonusCoracaoGrande(3);
        jogador2.setBonusCoracaoPequeno(2);
        jogador2.setCoracaoGrande(5);
        jogador2.setCoracaoPequeno(3);
        jogador2.adicionaCarta(carta);
        jogador2.adicionaObjetivo(cartaObjetivo);

        sala.setId(UUID.randomUUID());
        sala.setCartasObjetivo(new ArrayList<>());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);

        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
        sala.adicionarJogador(jogador2);

        salaRequest.setHash("hashpraentrar");
        salaRequest.setJogador(jogador2);

        salaResponse.setSala(sala);
        salaResponse.setJogador(jogador);
    }

    @Test
    @DisplayName("Teste para conectar ao jogo")
    void testConectarJogo() {
        sala.adicionarJogador(jogador2);
        try {
            when(webSocketServiceImpl.conectarJogo(jogador2, sala.getHash())).thenReturn(salaResponse);
            SalaResponse salaTarget = webSocketServiceImpl.conectarJogo(jogador2, sala.getHash());
            assertEquals(salaResponse, salaTarget);
        } catch (JogoInvalidoException e) {
            fail("Parametros nulos");
        }
    }

    @Test
    @DisplayName("Teste para conectar ao jogo com jogador nulo")
    void testConectarJogoComJogadorNull() throws JogoInvalidoException {

        when(webSocketServiceImpl.conectarJogo(null, sala.getHash())).thenReturn(salaResponse);
        assertEquals(salaResponse, webSocketServiceImpl.conectarJogo(null, sala.getHash()));
    }

    @Test
    @DisplayName("Teste para criar um jogo")
    void testCriarJogo() throws JogoInvalidoException {
        when(webSocketServiceImpl.criarJogo(jogador)).thenReturn(salaResponse);
        assertEquals(salaResponse, webSocketServiceImpl.criarJogo(jogador));
    }

    @Test
    @DisplayName("Teste para não criar jogo com parametro null")
    void testCriarJogoComErro() throws JogoInvalidoException {
        when(webSocketServiceImpl.criarJogo(null)).thenReturn(null);;
        assertNull(webSocketServiceImpl.criarJogo(null));
    }

    @Test
    @DisplayName("Teste para criar um jogador")
    void testCriaJogador() {
        when(webSocketServiceImpl.criarJogador(jogador, 2)).thenReturn(jogador);
        assertEquals(jogador, webSocketServiceImpl.criarJogador(jogador, 2));
    }

    @Test
    @DisplayName("Teste para não criar jogador com parametro null")
    void testCriaJogadorComErro()  {
        when(webSocketServiceImpl.criarJogador(null,null)).thenReturn(null);
        assertNull(webSocketServiceImpl.criarJogador(null,null));
    }


    @Test
    @DisplayName("Teste para ver número jogadores na sala")
    void testQuantidadeJogadores() {

        when(webSocketServiceImpl.getQuantidadeJogadores(sala.getHash())).thenReturn(numero);
        assertEquals(numero, webSocketServiceImpl.getQuantidadeJogadores(sala.getHash()));

    }

}

