package com.db.jogo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartaObjetivoTest {

    private CartaObjetivo cartaObjetivo1;

    @BeforeEach
    void init() {
        cartaObjetivo1 = CartaObjetivo.builder()
                .id(UUID.randomUUID())
                .textoRegra("regra")
                .pontos(2)
                .categoria("categoria")
                .textoTematico("tema")
                .tipoContagem(2)
                .tipo("tipo")
                .build();
    }

    @Test
    void construtor() {
        assertAll("cartaObjetivo1",
                () -> assertNotNull(cartaObjetivo1.getId()),
                () -> assertNotNull(cartaObjetivo1.getTextoRegra()),
                () -> assertNotNull(cartaObjetivo1.getPontos()),
                () -> assertNotNull(cartaObjetivo1.getTextoTematico()),
                () -> assertNotNull(cartaObjetivo1.getTipoContagem())
    );
    }

}