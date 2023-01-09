package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.db.jogo.model.Baralho;

import com.db.jogo.repository.BaralhoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("Baralho Service Teste")
class BaralhoServiceImplTest {

	@Mock
	private BaralhoRepository baralhoRepositoryMock;
	@InjectMocks
	private BaralhoServiceImpl baralhoServiceImpl;

	Baralho baralho;

	@BeforeEach
	public void init() {
		baralho = Baralho.builder()
		.id(UUID.fromString("fd7b6723-77e2-4846-bd22-88df15ca150a"))
		.codigo("LILA1")
		.titulo("Corações de Lila")
		.descricao("Jogo de cartas")
		.build();
	}

	@Test
	void deveVerificarSeEncontraBaralhoCodigoSucesso() {
		when(baralhoRepositoryMock.findByCodigo(baralho.getCodigo())).thenReturn((Optional.of(baralho)));
		Optional<Baralho> baralhoRetornado = baralhoServiceImpl.findByCodigo(baralho.getCodigo());
		assertEquals(Optional.of(baralho), baralhoRetornado);
	}

	@Test
	void deveVerificarSeEncontraBaralhoIdSucesso() {
		when(baralhoRepositoryMock.findById(baralho.getId().toString())).thenReturn(Optional.of(baralho));
		Baralho baralhoRetornado = baralhoServiceImpl.findById(baralho.getId().toString());
		assertEquals(baralho, baralhoRetornado);
	}

	@Test
	void deveVerificarSeEncontraBaralhoIdFalha() {
		when(baralhoRepositoryMock.findById(baralho.getId().toString())).thenReturn(null);
		Baralho baralhoRetornado = baralhoServiceImpl.findById(baralho.getId().toString());
		assertEquals(null, baralhoRetornado);
	}


	@Test
	void deveVerificarSeBaralhoSalvaTesteSucesso() {
		when(baralhoRepositoryMock.save(baralho)).thenReturn(baralho);
		baralhoServiceImpl.saveBaralho(baralho);
		verify(baralhoRepositoryMock, times(1)).save(baralho);
	}

}