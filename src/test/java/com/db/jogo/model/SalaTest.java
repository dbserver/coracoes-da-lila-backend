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
    void construtor() {
        assertAll("sala",
                () -> assertEquals(StatusEnum.NOVO, sala.getStatus()),
                () -> assertNull(sala.getDataHoraFimDoJogo()),
                () -> {
                    TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
                    assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDth_inicio()));
                });
    }

    @Test
    void generateHash() {
        String hash = sala.generateHash();
        assertThat(hash, CoreMatchers.not(is(emptyOrNullString())));
        assertThat(hash, CoreMatchers.not(is(blankString())));
    }

    @Test
    void deveRemoverCartaObjetivo() {
        assertFalse(sala.removerCartaDoObjetivo(cartaObjetivo1));
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
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDth_inicio()), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())));
        assertNotNull(sala.getDth_inicio());
    }

    @Test
    void getIdCartaInicio() {
        sala.getCartaInicioId();
        sala.setCartaInicioId(UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
        assertEquals(sala.getCartaInicioId(), UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
    }

    @Test
    void deveSortearCartaInicial() {
        List<CartaInicio> lista = new ArrayList<>();
        lista.add(cartaInicio1);
        lista.add(cartaInicio2);

        sala.sorteiaCartaInicial(lista);
        assertNotNull(sala.getCartaInicioId());
    }
}