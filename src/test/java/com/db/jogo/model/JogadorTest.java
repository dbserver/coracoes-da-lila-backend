package com.db.jogo.model;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.enums.StatusEnumJogador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JogadorTest {

    private Jogador jogador;
    private CartaDoJogo cartaDoJogo;
    private CartaDoJogo cartaDoJogo2;
    private CartaObjetivo cartaObjetivo1;
    private CartaObjetivo cartaObjetivo2;

    @BeforeEach
    void init() {
        cartaObjetivo1 = new CartaObjetivo();
        cartaObjetivo2 = new CartaObjetivo();
        cartaDoJogo2 = new CartaDoJogo();
        cartaDoJogo = CartaDoJogo.builder()
                .id(UUID.fromString("e4813862-1a8a-4e0d-94e1-59bbe2465ea0"))
                .tipo(CartaDoJogoEnumTipo.INFORMACAO)
                .categoria(CartaDoJogoEnumCategoria.VISUAL)
                .bonus(false)
                .texto("texto")
                .valorCoracaoPequeno(2)
                .valorCoracaoGrande(1)
                .fonte("fonte")
                .pontos(2)
                .build();
        jogador = new Jogador();
    }


    @Test
    void testaQueCartaDoJogoFoiAdicionada() {
        jogador.adicionaCarta(cartaDoJogo);
        CartaDoJogo cartaDoJogoAdicionada = jogador.getCartasDoJogo().get(0);
        int quantidadeDeCartasDoJogo = jogador.getCartasDoJogo().size();

        assertEquals(cartaDoJogo,cartaDoJogoAdicionada);
        assertEquals(1, quantidadeDeCartasDoJogo);
    }

    @Test
    void testaQueCartaDoJogoFoiRemovidaDoJogador() {
        jogador.adicionaCarta(cartaDoJogo);
        jogador.adicionaCarta(cartaDoJogo2);

        jogador.removeCarta(cartaDoJogo);

        int quantidadeDeCartasDoJogo = jogador.getCartasDoJogo().size();
        assertEquals(1, quantidadeDeCartasDoJogo);
    }

    @Test
    void adicionaObjetivo() {
        jogador.adicionaObjetivo(cartaObjetivo1);

        int quantidadeDeCartasObjetivo = jogador.getCartasObjetivo().size();
        assertEquals(1, quantidadeDeCartasObjetivo);
    }

    @Test
    void removeObjetivo() {
        jogador.adicionaObjetivo(cartaObjetivo1);
        jogador.adicionaObjetivo(cartaObjetivo2);

        jogador.removeObjetivo(cartaObjetivo1);

        int quantidadeDeCartasObjetivoDoJogador = jogador.getCartasObjetivo().size();
        assertEquals(1, quantidadeDeCartasObjetivoDoJogador);
    }
}