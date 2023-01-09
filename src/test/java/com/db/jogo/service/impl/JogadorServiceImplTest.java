package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.model.Jogador;

import com.db.jogo.repository.JogadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("Jogador Service Teste")
class JogadorServiceImplTest {

	@Mock
	private JogadorRepository jogadorRepositoryMock;

	@InjectMocks
    private JogadorServiceImpl jogadorServiceImpl;

	Jogador jogador;
	Jogador jogador2;
	@BeforeEach
	public void init() {
		jogador = Jogador.builder()
		.id(UUID.randomUUID())
		.nome("teste")
		.bonusCoracaoGrande(1)
		.bonusCoracaoPequeno(1)
		.coracaoGrande(1)
		.pontos(1)
		.coracaoPequeno(2)
		.build();
	}


	@Test
	void deveVerificarSeEncontraJogadorIdSucesso() {
		when(jogadorRepositoryMock.findById(jogador.getId())).thenReturn(Optional.of(jogador));
		Optional<Jogador> jogadorRetornado = jogadorServiceImpl.findById(jogador.getId());
		assertEquals(Optional.of(jogador), jogadorRetornado);
	}

	@Test
	void deveVerificarSeEncontraJogadorIdFalha() {
		when(jogadorRepositoryMock.findById(jogador.getId())).thenReturn(null);
		Optional<Jogador> jogadorRetornado = jogadorServiceImpl.findById(jogador.getId());
		assertEquals(null, jogadorRetornado);
	}

	@Test
	void deveVerificarSeSalvaJogador() {
		when(jogadorRepositoryMock.save(jogador)).thenReturn(jogador);
		jogadorServiceImpl.saveJogador(jogador);
		verify(jogadorRepositoryMock, times(1)).save(jogador);
	}

	@Test
	void deveVerificarSeAtualizaJogadorSucesso() {
		when(jogadorRepositoryMock.findById(jogador.getId())).thenReturn(Optional.of(jogador));
		jogadorServiceImpl.atualizarJogador(jogador);
		verify(jogadorRepositoryMock, times(1)).save(jogador);
	}
}