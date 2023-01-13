package com.db.jogo.model;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BaralhoTest {

    private Baralho baralho;
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
                .id(UUID.fromString("88eec20e-24d5-45e5-ae61-517b3beb35ca"))
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

        baralho = Baralho.builder()
                .id(UUID.randomUUID())
                .codigo("codigo")
                .descricao("descricao")
                .titulo("titulo")
                .build();

        baralho.adicionarCartaDoInicio(cartaInicio1);
        baralho.adicionarCartaDoObjetivo(cartaObjetivo1);
        baralho.adicionarCartadoJogo(cartaDoJogo1);
    }

    @Test
    void deveAdicionarCartaDoJogo() {
        baralho.adicionarCartadoJogo(cartaDoJogo2);
        CartaDoJogo cartaDoJogorRetornada = baralho.getCartasDoJogo().get(1);
        int quantidadeDeCartasDoJogoNoBaralho = baralho.getCartasDoJogo().size();
        assertEquals(cartaDoJogo2, cartaDoJogorRetornada);
        assertEquals(2, quantidadeDeCartasDoJogoNoBaralho);

    }

    @Test
    void testaQueConseguiuRemoverCartaDoJogo() {
        boolean removeuCartaDoJogo = baralho.removerCartaDoJogo(cartaDoJogo1);
        assertTrue(removeuCartaDoJogo);
    }

    @Test
    void testaQueNaoConseguiuRemoverCartaDoJogo() {
        CartaDoJogo cartaInexistenteNoBaralho = new CartaDoJogo();
        boolean removeuCartaDoJogo = baralho.removerCartaDoJogo(cartaInexistenteNoBaralho);
        assertFalse(removeuCartaDoJogo);
    }

    @Test
    void testaQueCartaCartaDoObjetivoFoiAdicionada() {
        baralho.adicionarCartaDoObjetivo(cartaObjetivo1);
        CartaObjetivo cartaAdicionada = baralho.getCartasObjetivo().get(1);
        int quantidadeDasCartasObjetivo = baralho.getCartasObjetivo().size();
        assertEquals(cartaObjetivo1, cartaAdicionada);
        assertEquals(2, quantidadeDasCartasObjetivo);
    }

    @Test
    void testaQueConseguiuRemoverCartaDoObjetivo() {
        boolean conseguiuRemover = baralho.removerCartaDoObjetivo(cartaObjetivo1);
        assertTrue(conseguiuRemover);
    }
    @Test
    void testaQueNaoConseguiuRemoverCartaDoObjetivo() {
        CartaObjetivo cartaObjetivo2 = new CartaObjetivo();
        boolean conseguiuRemover = baralho.removerCartaDoObjetivo(cartaObjetivo2);
        assertFalse(conseguiuRemover);
    }

    @Test
    void deveAdicionarCartaDoInicio() {

        baralho.adicionarCartaDoInicio(cartaInicio1);

        CartaInicio cartaInicioAdicionadaAoBaralho = baralho.getCartasInicio().get(1);
        assertEquals(cartaInicio1, cartaInicioAdicionadaAoBaralho);
    }

    @Test
    void testaQueConseguiuRemoverCartaDoInicio() {
        boolean conseguiuRemoverCartaDoInicio = baralho
                .removerCartaDoInicio(cartaInicio1);

        assertTrue(conseguiuRemoverCartaDoInicio);
    }
    @Test
    void testaQueNaoConseguiuRemoverCartaDoInicio() {
        CartaInicio cartaInicioNaoExistenteNoBaralho = new CartaInicio();
        boolean conseguiuRemoverCartaDoInicio = baralho
                .removerCartaDoInicio(cartaInicioNaoExistenteNoBaralho);

        assertFalse(conseguiuRemoverCartaDoInicio);
    }
}