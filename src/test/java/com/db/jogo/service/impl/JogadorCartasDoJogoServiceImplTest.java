package com.db.jogo.service.impl;

import com.db.jogo.model.JogadorCartasDoJogo;
import com.db.jogo.repository.JogadorCartasDoJogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class JogadorCartasDoJogoServiceImplTest {

    JogadorCartasDoJogo jogadorCartasDoJogo;
    @Mock
    JogadorCartasDoJogoRepository jogadorCartasDoJogoRepositoryMock;

    @InjectMocks
    JogadorCartasDoJogoServiceImpl jogadorCartasDoJogoServiceImpl;

    @BeforeEach
    void setUp() {
        UUID cartaID = UUID.fromString("b735c4d0-c53a-4b8f-a759-a557fe9de514");
        UUID jogadorID = UUID.fromString("245d3182-4fa1-4e04-93ed-78d88c5f1762");

        jogadorCartasDoJogo = JogadorCartasDoJogo.builder()
                .jogadorID(jogadorID)
                .cartaDoJogoID(cartaID)
                .build();
    }

    @Test
    void saveJogadorCartasDoJogo() {
        jogadorCartasDoJogo = JogadorCartasDoJogo.builder().build();

        when(jogadorCartasDoJogoRepositoryMock.save(jogadorCartasDoJogo)).thenReturn(jogadorCartasDoJogo);

        JogadorCartasDoJogo jogadorCartasDoJogoSalvo = jogadorCartasDoJogoServiceImpl.saveJogadorCartasDoJogo(jogadorCartasDoJogo);

        assertNotNull(jogadorCartasDoJogoSalvo);
        assertEquals(jogadorCartasDoJogo, jogadorCartasDoJogoSalvo);
    }

    @Test
    void findByJogadorIDAndCartaDoJogoID() {
        UUID jogadorID = jogadorCartasDoJogo.getJogadorID();
        UUID cartaDoJogoID = jogadorCartasDoJogo.getCartaDoJogoID();
        when(jogadorCartasDoJogoRepositoryMock.findByJogadorIDAndCartaDoJogoID(jogadorID, cartaDoJogoID)).thenReturn(jogadorCartasDoJogo);

        JogadorCartasDoJogo jogadorComCartaDoJogo = jogadorCartasDoJogoServiceImpl
                .findByJogadorIDAndCartaDoJogoID(jogadorID, cartaDoJogoID);

        assertNotNull(jogadorComCartaDoJogo);
        assertEquals(jogadorCartasDoJogo, jogadorComCartaDoJogo);
    }
}