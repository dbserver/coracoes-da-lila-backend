package com.db.jogo.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BaralhoTest {



    @Test
    void getIdCartaInicio() {
        Baralho baralho = Mockito.spy(new Baralho());
        baralho.getIdCartaInicio();
        baralho.setIdCartaInicio(UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
        assertEquals(baralho.getIdCartaInicio(), UUID.fromString("d1516d33-ff6f-4dc9-aedf-9316421096cb"));
    }
}