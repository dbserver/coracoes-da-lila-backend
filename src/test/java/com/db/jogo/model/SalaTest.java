package com.db.jogo.model;

import com.db.jogo.enums.StatusEnum;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsBlankString.blankString;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.*;

class SalaTest {

    private Sala sala;
    private CartaObjetivo cartaObjetivo1;
    private List<CartaInicio> listaCartas;
    private CartaInicio cartaInicio1, cartaInicio2;

    @BeforeEach
    void init() {
        cartaObjetivo1 = new CartaObjetivo();
        sala = new Sala();
        sala.setDth_inicio(Timestamp.valueOf("2022-11-20 11:21:12"));
        cartaInicio1 = CartaInicio.builder()
                .id(UUID.fromString("e4813862-1a8a-4e0d-94e1-59bbe2465ea2"))
                .nome("nome")
                .descricao("descricao")
                .build();
        cartaInicio2 = CartaInicio.builder()
                .id(UUID.fromString("f4813862-1a8a-4e0d-94e1-59bbe2465ea2"))
                .nome("nome")
                .descricao("descricao")
                .build();
    }

    @Test
    void generateHash() {
        String hash = sala.generateHash();
        int hashOitoCaracteres = hash.length();
        String hashAletoriaTemQueSerDiferente = "xhqJuqzw";
        assertNotNull(hash);
        assertNotEquals(hashAletoriaTemQueSerDiferente, hash);
        assertEquals(8, hashOitoCaracteres);
    }

    @Test
    void adicionarCartaDoObjetivo() {
        List<CartaObjetivo> listaCartasObjetivo = new ArrayList<>();
        sala.setCartasObjetivo(listaCartasObjetivo);
        sala.adicionarCartaDoObjetivo(cartaObjetivo1);
        int quantidadeDeCartasObjetivo = sala.getCartasObjetivo().size();
        assertEquals(1, quantidadeDeCartasObjetivo);
    }

    @Test
    void deveRemoverCartaObjetivo() {
        List<CartaObjetivo> listaCartasObjetivo = new ArrayList<>();
        sala.setCartasObjetivo(listaCartasObjetivo);
        sala.adicionarCartaDoObjetivo(cartaObjetivo1);

        sala.removerCartaDoObjetivo(cartaObjetivo1);
        assertEquals(true, sala.getCartasObjetivo().isEmpty());
    }
    @Test
    void testeDeveAdicionarJogador() {
        List<Jogador> jogadores = new ArrayList<>();
        sala.setJogadores(jogadores);
        sala.adicionarJogador(new Jogador());

        int quantidadeDeJogadoresNaSala = sala.getJogadores().size();
        assertEquals(1, quantidadeDeJogadoresNaSala);
    }

    @Test
    void testeDeveRemoverJogador() {
        Jogador jogador = new Jogador();
        Jogador jogador2 = new Jogador();
        List<Jogador> jogadores = new ArrayList<>();
        jogadores.add(jogador);
        jogadores.add(jogador2);
        sala.setJogadores(jogadores);

        sala.removerJogador(jogador);
        int quantidadeJogadoresNaSala = sala.getJogadores().size();
        assertEquals(1, quantidadeJogadoresNaSala);
    }

    @Test
    void deveSortearCartaInicial() {
        List<CartaInicio> lista = new ArrayList<>();
        lista.add(cartaInicio1);
        lista.add(cartaInicio2);

        sala.sorteiaCartaInicial(lista);
        assertNotNull(sala.getCartaInicioId());
    }

    @Test
    void setDataHoraFimDeJogo() {
        sala.setDataHoraFimDeJogo();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())),
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDataHoraFimDoJogo()));
    }

    @Test
    @DisplayName("Teste para ver se a variavel DataHoraFimDoJogo está sendo gravada")
    void getDataHoraFimDeJogo() {
        sala.setStatus(StatusEnum.FINALIZADO);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(sala.getDataHoraFimDoJogo()), new SimpleDateFormat("yyyy-MM-dd hh:mm").format(Timestamp.from(Instant.now())));
        assertNotNull(StatusEnum.FINALIZADO);
    }

    @Test
    void setStatus() {
        sala.setStatus(StatusEnum.FINALIZADO);
        assertAll("sala",
                () -> assertEquals(StatusEnum.FINALIZADO, sala.getStatus()),
                () -> {
                    TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
                    assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(Timestamp.from(Instant.now())),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm").format(sala.getDataHoraFimDoJogo()));
                }
        );
    }

    @Test
    @DisplayName("Teste para ver se a Tdh_Inicio está sendo gravada")
    void getDth_inicio() {
        String data = "2022-11-20 11:21:12";
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
        assertEquals(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(sala.getDth_inicio()), new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(Timestamp.valueOf(data)));
        assertNotNull(sala.getDth_inicio());
    }

    @Test
    void getIdCartaInicio() {
        sala.getCartaInicioId();
        sala.setCartaInicioId(UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
        assertEquals(sala.getCartaInicioId(), UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
    }

}