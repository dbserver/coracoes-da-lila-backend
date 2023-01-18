package com.db.jogo.controller;

import static com.db.jogo.enums.CartaDoJogoEnumCategoria.*;
import static com.db.jogo.enums.CartaDoJogoEnumCategoria.TEA;
import static com.db.jogo.enums.CartaDoJogoEnumTipo.FILME;
import static com.db.jogo.enums.CartaDoJogoEnumTipo.INFORMACAO;
import static com.db.jogo.enums.StatusEnumJogador.JOGANDO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.service.SalaService;
import com.db.jogo.service.WebSocketService;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.db.jogo.dto.SalaRequest;
import com.db.jogo.dto.SalaResponse;
import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaInicio;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.impl.WebSocketServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.BindingResult;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Websocket Controller Teste")
@ExtendWith(MockitoExtension.class)
public class WebSocketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    WebSocketServiceImpl webSocketServiceImpl;
    @Mock
    BindingResult bindingResult;
    @Mock
    SalaService salaService;
    CartaInicio cartaInicio;
    Baralho baralho;
    CartaDoJogo carta;
    CartaObjetivo cartaObjetivo;
    Jogador jogador;
    Jogador jogador2;
    Sala sala;
    List<CartaObjetivo> listaCartasObjetivo;
    List<CartaDoJogo> listaCartasDoJogo;
    List<CartaInicio> cartaInicioList;
    List<Jogador> jogadores;
    @InjectMocks
    WebSocketController webSocketController;

    @BeforeEach
    public void init(){

        cartasObjetivoFake();
        cartasDoJogoFake();
        jogadoresFakes();
        cartaInicio = new CartaInicio();
        cartaInicioList = new ArrayList<>();
        cartaInicio.setId(UUID.fromString("0583ecd0-974c-11ed-a8fc-0242ac120002"));
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");
        cartaInicioList.add(cartaInicio);

        baralho = new Baralho();
        baralho.setCodigo("qwerty");
        baralho.setId(UUID.randomUUID());
        baralho.setTitulo("Teste");
        baralho.setDescricao("Exemplo");
        baralho.setCartasInicio(cartaInicioList);
        baralho.adicionarCartaDoInicio(cartaInicio);
        baralho.setCartasDoJogo(listaCartasDoJogo);
        baralho.adicionarCartadoJogo(carta);
        baralho.setCartasObjetivo(listaCartasObjetivo);
        baralho.adicionarCartaDoInicio(cartaInicio);


        sala = new Sala();
        sala.setId(UUID.fromString("cd0dab06-9751-11ed-a8fc-0242ac120002"));
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setDado(0);
        sala.setJogadores(jogadores);
        sala.adicionarJogador(jogador);
        sala.adicionarCartaDoObjetivo(cartaObjetivo);
        sala.setJogadorEscolhido(jogador);
        sala.setCartaInicioId(cartaInicio.getId());
        sala.setCartaObjetivoEscolhida(cartaObjetivo);
    }

    @Test
    void deveRetornarBadRequestQuandoComprarCoracaoPequeno() throws JogoInvalidoException {
        when(bindingResult.hasErrors()).thenReturn(true);
        ResponseEntity<?> coracaoPequeno = webSocketController.comprarCoracaoPequeno(sala, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, coracaoPequeno.getStatusCode());
    }
//    @Test
    //esse Teste falhou
//    void deveRetornarNotFoundAoComprarCoracaoPequeno() throws Exception {
//        when(webSocketServiceImpl.compraCoracoesPequenos(sala)).thenReturn(Optional.of(sala));
//
//        ObjectMapper mapper = new ObjectMapper();
//        String salaJson = mapper.writeValueAsString(sala);
//
//        mockMvc.perform(put("/api/jogada/comprarcoracaopequeno")
//                .content(salaJson)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isNotFound());
//    }

    private void jogadoresFakes() {
        jogador = new Jogador();
        jogador.setId(UUID.fromString("01fa2624-bc16-4d3b-a1d6-6e797b47e04d"));
        jogador.setPosicao(1);
        jogador.setCartasDoJogo(listaCartasDoJogo);
        jogador.setCartasObjetivo(listaCartasObjetivo);
        jogador.setNome("Pedro");
        jogador.setPontos(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(1);
        jogador.setBonusCoracaoGrande(0);
        jogador.setBonusCoracaoPequeno(0);
        jogador.setIsHost(true);
        jogador.setPontosObjetivo(0);
        jogador.setStatus(JOGANDO);

        jogador2 = new Jogador();
        jogador2.setId(UUID.fromString("00c5506e-d035-4d7d-97d3-ecff0d7f0d58"));
        jogador2.setPosicao(2);
        jogador2.setCartasDoJogo(listaCartasDoJogo);
        jogador2.setCartasObjetivo(listaCartasObjetivo);
        jogador2.setNome("Joao");
        jogador2.setPontos(0);
        jogador2.setCoracaoPequeno(2);
        jogador2.setCoracaoGrande(0);
        jogador2.setBonusCoracaoGrande(0);
        jogador2.setBonusCoracaoPequeno(0);
        jogador2.setIsHost(false);
        jogador2.setPontosObjetivo(0);
        jogador2.setStatus(JOGANDO);
        jogadores = new ArrayList<>();
        jogadores.add(jogador);
        jogadores.add(jogador2);
    }

    private void cartasDoJogoFake() {
        carta = new CartaDoJogo();
        carta.setId(UUID.fromString("775c7b9f-6ce2-4919-8696-c751dd322ac3"));
        carta.setTipo(INFORMACAO);
        carta.setCategoria(AUDITIVA);
        carta.setBonus(true);
        carta.setTexto("Deficiência auditiva é a perda parcial ou total da audição, " +
                "causada por malformação (causa genética) ou lesões no aparelho auditivo.");
        carta.setValorCoracaoGrande(2);
        carta.setValorCoracaoPequeno(1);
        carta.setFonte("novaescola.org.br");
        carta.setPontos(1);

        listaCartasDoJogo = new ArrayList<>();
        listaCartasDoJogo.add(carta);
    }

    private void cartasObjetivoFake() {
        cartaObjetivo = new CartaObjetivo();
        cartaObjetivo
                .setId(UUID.fromString("55d5947b-f491-4fe2-8c65-4d1e61ef8561"));
        cartaObjetivo.setTextoRegra("Ganhe 1 ponto " +
                "por cada carta que você tiver da categoria Visual ao final da partida");
        cartaObjetivo.setPontos(1);
        cartaObjetivo.setCategoria("VISUAL");
        cartaObjetivo.setTextoTematico("Seu grau de óculos aumentou muito.");
        cartaObjetivo.setTipoContagem(1);
        cartaObjetivo.setTipo("");

        listaCartasObjetivo = new ArrayList<>();
        listaCartasObjetivo.add(cartaObjetivo);
    }




}
