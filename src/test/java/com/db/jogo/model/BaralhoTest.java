package com.db.jogo.model;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BaralhoTest {

    private Baralho baralho1;
    private CartaDoJogo cartaDoJogo1;
    private CartaDoJogo cartaDoJogo2;
    private CartaObjetivo cartaObjetivo1;
    private CartaInicio cartaInicio1;

    @BeforeEach
    void init() {
        cartaDoJogo1 = CartaDoJogo.builder()
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

        cartaDoJogo2 = CartaDoJogo.builder()
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
        cartaObjetivo1 = CartaObjetivo.builder()
                .id(UUID.fromString("e4813862-1a8a-4e0d-94e1-59bbe2465ea1"))
                .textoRegra("texto")
                .pontos(2)
                .categoria("categoria")
                .textoTematico("texto")
                .tipoContagem(2)
                .tipo("tipo")
                .build();
        cartaInicio1 = CartaInicio.builder()
                .id(UUID.fromString("e4813862-1a8a-4e0d-94e1-59bbe2465ea2"))
                .nome("nome")
                .descricao("descricao")
                .build();

        baralho1 = Baralho.builder()
                .id(UUID.randomUUID())
                .codigo("codigo")
                .descricao("descricao")
                .titulo("titulo")
                .build();

        baralho1.adicionarCartaDoInicio(cartaInicio1);
        baralho1.adicionarCartaDoObjetivo(cartaObjetivo1);
        baralho1.adicionarCartadoJogo(cartaDoJogo1);
    }

    @Test
    void construtor() {
        assertNotNull(baralho1.getId());
        assertNotNull(baralho1.getCodigo());
        assertNotNull(baralho1.getTitulo());
        assertNotNull(baralho1.getDescricao());
        assertNotNull(baralho1.getCartasObjetivo());
        assertNotNull(baralho1.getCartasDoJogo());
        assertNotNull(baralho1.getCartasInicio());
    }

    @Test
    void deveAdicionarCartaDoJogo() {
        baralho1.adicionarCartadoJogo(cartaDoJogo2);
        assertEquals(baralho1.getCartasDoJogo().get(1), cartaDoJogo2);
    }

    @Test
    void deveRemoverCartaDoJogo() {
        baralho1.removerCartaDoJogo(cartaDoJogo1);
        assertEquals(baralho1.getCartasDoJogo(), List.of());
    }

    @Test
    void deveAdicionarCartaDoObjetivo() {
        baralho1.adicionarCartaDoObjetivo(cartaObjetivo1);
        assertEquals(baralho1.getCartasObjetivo().get(0), cartaObjetivo1);
    }

    @Test
    void deveRemoverCartaDoObjetivo() {
        baralho1.adicionarCartaDoObjetivo(cartaObjetivo1);
        baralho1.removerCartaDoObjetivo(cartaObjetivo1);
        assertEquals(baralho1.getCartasObjetivo(), List.of());
    }

    @Test
    void deveAdicionarCartaDoInicio() {
        baralho1.adicionarCartaDoInicio(cartaInicio1);
        assertEquals(baralho1.getCartasInicio().get(0), cartaInicio1);
    }

    @Test
    void deveRemoverCartaDoInicio() {
        baralho1.adicionarCartaDoInicio(cartaInicio1);
        baralho1.removerCartaDoInicio(cartaInicio1);
        assertEquals(baralho1.getCartasInicio(), List.of());
    }
}