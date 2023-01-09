package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.db.jogo.model.CartaObjetivo;

import com.db.jogo.repository.BaralhoRepository;
import com.db.jogo.repository.CartaObjetivoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartaObjetivo Service Teste")
class CartaObjetivoServiceImplTest {

	@Mock
	private CartaObjetivoRepository cartaObjetivoRepositoryMock;
	@InjectMocks
	private CartaObjetivoServiceImpl cartaObjetivoServiceImpl;

	CartaObjetivo cartaObjetivo;

	@BeforeEach
	public void init() {
		cartaObjetivo = CartaObjetivo.builder()
		.id(UUID.randomUUID())
		.tipoContagem(2)
		.tipo("FILME")
		.categoria("")
		.textoRegra("Ganhe 2 pontos")
		.textoTematico("Lorem ipsum")
		.pontos(3)
		.build();
	}

	@Test
	void deveVerificarSeEncontraCartaIdSucesso() {
		when(cartaObjetivoRepositoryMock.findById(cartaObjetivo.getId())).thenReturn(Optional.of(cartaObjetivo));
		Optional<CartaObjetivo> cartaObjetivoRetornada = cartaObjetivoServiceImpl.findById(cartaObjetivo.getId());
		assertEquals(Optional.of(cartaObjetivo), cartaObjetivoRetornada);
	}

	@Test
	void deveVerificarSeEncontraCartaIdFalha() {
		when(cartaObjetivoRepositoryMock.findById(cartaObjetivo.getId())).thenReturn(null);
		Optional<CartaObjetivo> cartaObjetivoRetornada = cartaObjetivoServiceImpl.findById(cartaObjetivo.getId());
		assertEquals(null, cartaObjetivoRetornada);
	}

	@Test
	void deveVerificarSeSalvaCartaObjetivo() {
		when(cartaObjetivoRepositoryMock.save(cartaObjetivo)).thenReturn(cartaObjetivo);
		cartaObjetivoServiceImpl.saveCartaObjetivo(cartaObjetivo);
		verify(cartaObjetivoRepositoryMock, times(1)).save(cartaObjetivo);
	}

}