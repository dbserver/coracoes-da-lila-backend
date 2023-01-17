package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
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

import javax.swing.text.html.Option;


@ExtendWith(MockitoExtension.class)
@DisplayName("Jogador Service Teste")
class JogadorServiceImplTest {

	@Mock
	private JogadorRepository jogadorRepositoryMock;

	@InjectMocks
    private JogadorServiceImpl jogadorServiceImpl;

	private Jogador jogador;
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
		when(jogadorRepositoryMock.save(jogador)).thenReturn(jogador);
		Optional<Jogador> jogadorEncontradoParaAtualizar = jogadorServiceImpl.atualizarJogador(jogador);
		verify(jogadorRepositoryMock, times(1)).save(jogador);
		assertEquals(Optional.of(jogador),jogadorEncontradoParaAtualizar);
	}
	@Test
	void naoDeveVerificarSeAtualizaJogador() {
		when(jogadorRepositoryMock.findById(jogador.getId())).thenReturn(null);
		assertThrows(IllegalArgumentException.class, () -> jogadorServiceImpl.atualizarJogador(jogador),
				"Impossível fazer atualização do objeto passado! ");
	}
	@Test
	void deveRetornarOptionalEmpty() {
		when(jogadorRepositoryMock.findById(jogador.getId())).thenReturn(Optional.empty());
		Optional<Jogador> jogadorOptionalEmpty = jogadorServiceImpl.atualizarJogador(jogador);
		assertEquals(Optional.empty(), jogadorOptionalEmpty);
	}

	@Test
	void deveriaRetornarTotalDeJogadores() {
		when(jogadorRepositoryMock.findAll()).thenReturn(List.of(jogador, jogador));

		int quantidadeDeJogadoresNaSala = jogadorServiceImpl.totalJogadores();

		assertNotNull(quantidadeDeJogadoresNaSala);
		assertEquals(2, quantidadeDeJogadoresNaSala);
	}

	@Test
	void deveriaRetornarTotalDeJogadoresIgualZero() {
		when(jogadorRepositoryMock.findAll()).thenReturn(List.of());

		int quantidadeDeJogadoresNaSala = jogadorServiceImpl.totalJogadores();

		assertNotNull(quantidadeDeJogadoresNaSala);
		assertEquals(0, quantidadeDeJogadoresNaSala);
	}

    @Test
    void deveriaEncontrarTodosOsJogadores() {
		Iterable<Jogador> jogadorServiceIterable = List.of(jogador);
		when(jogadorRepositoryMock.findAll()).thenReturn(List.of(jogador));

		Iterable<Jogador> encontrarTodosOsJogadores = jogadorServiceImpl.findAll();

		assertNotNull(encontrarTodosOsJogadores);
		assertEquals(jogadorServiceIterable, encontrarTodosOsJogadores);
	}

	@Test
	void deveriaPermitirInicioDaPartida() {
		when(jogadorRepositoryMock.findAll()).thenReturn(List.of(jogador, jogador));

		Boolean temQuantidadeMinimaDeJogadores = jogadorServiceImpl.podeJogar();

		assertTrue(temQuantidadeMinimaDeJogadores);
		assertEquals(2,jogadorServiceImpl.totalJogadores());
	}

	@Test
	void naoDeveriaPermitirInicioDaPartida() {
		when(jogadorRepositoryMock.findAll()).thenReturn(List.of(jogador));

		Boolean temQuantidadeMinimaDeJogadores = jogadorServiceImpl.podeJogar();

		assertFalse(temQuantidadeMinimaDeJogadores);
		assertEquals(1,jogadorServiceImpl.totalJogadores());
	}

}