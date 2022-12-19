package com.db.jogo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

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
import com.db.jogo.service.SalaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Sala Controller Teste")
class SalaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    private SalaService salaService;

    CartaInicio cartaInicio = new CartaInicio();
    Baralho baralho = new Baralho();
    CartaDoJogo carta = new CartaDoJogo();
    CartaObjetivo cartaObjetivo = new CartaObjetivo();
    Jogador jogador = new Jogador();
    Sala sala = new Sala();
    Integer i = 0;
    @BeforeEach
    public void init(){
        cartaInicio.setId(UUID.randomUUID());
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");

        carta.setId(UUID.randomUUID());
        carta.setPontos(2);
        carta.setBonus(true);
        carta.setCategoria(CartaDoJogoEnumCategoria.VISUAL);
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

        baralho.setId(UUID.randomUUID());
        baralho.setId(UUID.randomUUID());
        baralho.setCodigo("LILA");
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
        jogador.setCartasDoJogo(new ArrayList<>());
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        jogador.setIsHost(true);
        jogador.adicionaCarta(carta);
        jogador.adicionaObjetivo(cartaObjetivo);

        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.setDado(0);

        sala.adicionarJogador(jogador);
    }

    @Test
    @DisplayName("Teste de Salvar/Criar uma sala do Controller de Sala")
    void criarSala() throws Exception {

        given(salaService.saveSala(sala)).willReturn(sala);

        ObjectMapper mapper = new ObjectMapper();
        String newSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(post("/sala")
                .content(newSalaAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Teste de encontrar sala por hash do Controller de Sala")
    void encontrarSalaPorHash() throws Exception{

        given(salaService.findSalaByHash(sala.getHash())).willReturn(Optional.of(sala));

        ObjectMapper mapper = new ObjectMapper();
        String encontrarSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(get("/sala/" + sala.getHash())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(encontrarSalaAsJSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Teste de Salvar/Criar uma sala do Controller de Sala com erro")
    void criarSalaComErro() throws Exception {
        Sala sala = new Sala();

        given(salaService.saveSala(sala)).willReturn(sala);

        ObjectMapper mapper = new ObjectMapper();
        String newSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(post("/sala")
                .content(newSalaAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Teste de encontrar sala por hash do Controller de Sala com erro")
    void encontrarSalaPorHashComErro() throws Exception{
        Sala sala = new Sala();
        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);

        given(salaService.findSalaByHash("hashpraentrarerrado")).willReturn(Optional.of(sala));

        ObjectMapper mapper = new ObjectMapper();
        String encontrarSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(get("/sala/" + sala.getHash())
                .content(encontrarSalaAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("Teste total Jogadores do Controller")
    void totalJogadores() throws Exception{

        Sala sala = new Sala();
        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);

        Integer i = 0;
        when(salaService.totalJogadores(sala.getHash())).thenReturn(i);
        assertEquals(i, salaService.totalJogadores(sala.getHash()));
    }


    @Test
    @DisplayName("Teste primeiroAJogar do Controller")
    void primeiroAJogar() throws Exception{

        Jogador jogador = new Jogador();

        Sala sala = new Sala();
        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);



        given(salaService.findFirst(sala.getHash())).willReturn(jogador);

        ObjectMapper mapper = new ObjectMapper();
        String primeiroAJogarAsJSON = mapper.writeValueAsString(jogador);
        this.mockMvc.perform(get("/sala/" + sala.getHash() + "/host")
                .content(primeiroAJogarAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isFound());
    }
}