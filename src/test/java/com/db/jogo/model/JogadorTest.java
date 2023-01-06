package com.db.jogo.model;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.service.CartaDoJogoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JogadorTest {

    private Jogador jogador1;
    private CartaDoJogo carta1;
    private CartaDoJogo carta2;
    private CartaObjetivo cartaObjetivo1;
    private CartaObjetivo cartaObjetivo2;

    @BeforeEach
    void init() {
        cartaObjetivo1 = new CartaObjetivo();
        cartaObjetivo2 = new CartaObjetivo();
        carta1 = new CartaDoJogo();
        carta2 = CartaDoJogo.builder()
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
        jogador1 = Jogador.builder()
                .id(UUID.randomUUID())
                .posicao(2)
                .cartasDoJogo(List.of(carta1))
                .cartasObjetivo(List.of(cartaObjetivo1))
                .nome("nome")
                .pontos(2)
                .coracaoGrande(1)
                .coracaoPequeno(2)
                .bonusCoracaoGrande(0)
                .bonusCoracaoPequeno(0)
                .isHost(false)
                .status(StatusEnumJogador.ESPERANDO)
                .build();
    }

    @Test
    void construtor() {
                assertNotNull(jogador1.getId());
                assertNotNull(jogador1.getPosicao());
                assertNotNull(jogador1.getNome());
                assertNotNull(jogador1.getPontos());
                assertNotNull(jogador1.getCoracaoPequeno());
                assertNotNull(jogador1.getCoracaoGrande());
                assertNotNull(jogador1.getBonusCoracaoGrande());
                assertNotNull(jogador1.getBonusCoracaoPequeno());
                assertNotNull(jogador1.getIsHost());
    }
}