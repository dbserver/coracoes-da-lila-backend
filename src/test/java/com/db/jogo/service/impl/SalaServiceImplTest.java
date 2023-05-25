package com.db.jogo.service.impl;

import com.db.jogo.enums.StatusEnum;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaServiceImplTest {

    @Mock
    SalaRepository salaRepositoryMock;

    @InjectMocks
    SalaServiceImpl salaServiceImpl;

    Sala sala;
    Jogador jogador;

    @BeforeEach
    void setUp() {
        jogador = new Jogador();

        sala = new Sala();
        sala.setHash("HOhccaUD");
        sala.setDado(1);
        sala.setCartaInicioId(UUID.randomUUID());
        sala.setDth_inicio(Timestamp.from(Instant.now()));
    }

    @Test
    void deveriaEncontrarSalaPorHash() {
        when(salaRepositoryMock.findSalaByHash(sala.getHash())).thenReturn(Optional.of(sala));

        Optional<Sala> salaEncontradaPorHash = salaServiceImpl.findSalaByHash(sala.getHash());

        assertNotNull(salaEncontradaPorHash);
        assertEquals(Optional.of(sala), salaEncontradaPorHash);
        assertEquals(sala.getHash(), salaEncontradaPorHash.get().getHash());
    }

    @Test
    void deveriaSalvarSala() {
        when(salaRepositoryMock.save(sala)).thenReturn(sala);

        Sala salaSalva = salaServiceImpl.saveSala(sala);
        assertNotNull(salaSalva);
        assertEquals(sala, salaSalva);
        assertEquals(sala.getHash(), salaSalva.getHash());
    }

    @Test
    void deveriaFazerAPrimeiraJogada() {
        Sala salaNaPrimeiraJogada = salaServiceImpl.jogada(sala);
        assertNotNull(salaNaPrimeiraJogada);
        assertEquals(sala, salaNaPrimeiraJogada);
    }

    @Test
    void deveriaRetornarTotalDeJogadoresNaSala() {
        jogador = Jogador.builder().build();
        sala.setJogadores(List.of(jogador));
        when(salaRepositoryMock.findSalaByHash(sala.getHash())).thenReturn(Optional.of(sala));

        Integer totalDeJogadores = salaServiceImpl.totalJogadores(sala.getHash());

        assertNotNull(totalDeJogadores);
        assertEquals(1, totalDeJogadores);
    }

    @Test
    void deveriaRetornarZeroComoTotalDeJogadoresNaSala() {
        when(salaRepositoryMock.findSalaByHash(sala.getHash())).thenReturn(Optional.empty());

        Integer totalDeJogadores = salaServiceImpl.totalJogadores(sala.getHash());

        assertNotNull(totalDeJogadores);
        assertEquals(0, totalDeJogadores);
    }

    @Test
    void deveriaRetornarPrimeiroJogador() {
        sala.setJogadores(List.of(jogador, jogador));

        when(salaRepositoryMock.findSalaByHash(sala.getHash())).thenReturn(Optional.of(sala));

        Jogador primeiroJogador = salaServiceImpl.findFirst(sala.getHash());

        assertNotNull(primeiroJogador);
        assertEquals(jogador, primeiroJogador);
    }

    @Test
    void naoDeveriaRetornarPrimeiroJogador() {
        sala.setJogadores(List.of());
        when(salaRepositoryMock.findSalaByHash(sala.getHash())).thenReturn(Optional.of(sala));

        Jogador primeiroJogador = salaServiceImpl.findFirst(sala.getHash());

        assertNull(primeiroJogador);
    }

}