package com.db.jogo.model;

import static com.db.jogo.enums.CartaDoJogoEnumCategoria.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class JogadorCartasDoJogoTest {
    private JogadorCartasDoJogo jogadorCartasDoJogo;
    private Jogador jogador;
    private CartaDoJogo cartaDoJogo;

    @BeforeEach
    void init() {

        jogadorCartasDoJogo = new JogadorCartasDoJogo();


        jogadorCartasDoJogo.setId(UUID.randomUUID());
        jogadorCartasDoJogo.setJogadorID(UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
        jogadorCartasDoJogo.setCartaDoJogoID(UUID.fromString("bb1ebadf-50c8-463c-8eb3-6f3998a466f3"));

        jogador = new Jogador();

        jogador.setId(UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));

        cartaDoJogo = new CartaDoJogo();

        cartaDoJogo.setId(UUID.fromString("bb1ebadf-50c8-463c-8eb3-6f3998a466f3"));
        jogadorCartasDoJogo = new JogadorCartasDoJogo(jogador, cartaDoJogo);
    }

    @Test
    void constructor() {
        JogadorCartasDoJogo jogadorBuilder = JogadorCartasDoJogo.builder().id(UUID.randomUUID()).build();
        assertAll("jogadorCartasDoJogo",
                () -> assertEquals(jogador.getId(), jogadorCartasDoJogo.getJogadorID()),
                () -> assertEquals(cartaDoJogo.getId(), jogadorCartasDoJogo.getCartaDoJogoID()),
                () -> assertNull(jogadorCartasDoJogo.getNovaCategoria()),
                () -> assertNotNull(jogadorBuilder.getId())
        );
    }

    @Test
    void testaJogadorCartasDoJogoOverflow() {
        assertEquals(jogador.getId(), jogadorCartasDoJogo.getJogadorID());

        assertEquals(cartaDoJogo.getId(), jogadorCartasDoJogo.getCartaDoJogoID());
    }
}
