package com.db.jogo.model;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartaInicioTest {

    private CartaInicio cartaInicio1;

    @Test
    void construtorBuild() {
        cartaInicio1 = CartaInicio.builder()
                .id(UUID.randomUUID())
                .nome("nome")
                .descricao("descricao")
                .build();

        assertNotNull(cartaInicio1.getId());
        assertNotNull(cartaInicio1.getNome());
        assertNotNull(cartaInicio1.getDescricao());
    }
}