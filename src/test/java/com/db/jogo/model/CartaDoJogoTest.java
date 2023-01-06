package com.db.jogo.model;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartaDoJogoTest {

    private CartaDoJogo cartaDoJogo1;

    @BeforeEach
    void init() {
        cartaDoJogo1 = CartaDoJogo.builder()
                .id(UUID.fromString("e4813862-1a8a-4e0d-94e1-59bbe2465ea0"))
                .tipo(CartaDoJogoEnumTipo.INFORMACAO)
                .categoria(CartaDoJogoEnumCategoria.VISUAL)
                .valorCoracaoPequeno(2)
                .valorCoracaoGrande(1)
                .bonus(false)
                .texto("texto")
                .fonte("fonte")
                .pontos(2)
                .build();
    }

    @Test
    void construtor() {
        assertAll("cartaDoJogo",
                () -> assertNotNull(cartaDoJogo1.getId()),
                () -> assertNotNull(cartaDoJogo1.getBonus()),
                () -> assertNotNull(cartaDoJogo1.getTexto()),
                () -> assertNotNull(cartaDoJogo1.getFonte()),
                () -> assertNotNull(cartaDoJogo1.getPontos())
                );
    }

}