package com.db.jogo.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.service.impl.CartaObjetivoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.BindingResult;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CartaObjetivoController.class)
@DisplayName("Carta Objetivo Controller Teste")
class CartaObjetivoControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	CartaObjetivoServiceImpl cartaObjetivoService;
	@Mock
	BindingResult bindingResult;
	@InjectMocks
	CartaObjetivoController cartaObjetivoController;

	String id = "272f930e-1adc-4405-b4a5-e9b909ce5738";
	CartaObjetivo newCartaObjetivo = CartaObjetivo.builder()
			.id(UUID.fromString(id))
			.tipoContagem(2)
			.tipo("FILME")
			.categoria("")
			.textoRegra("Ganhe 2 pontos se você tiver alguma carta de filme ao final da partida")
			.textoTematico("Sua sobrinha adolescente se identifica com personagens.")
			.pontos(2)
			.build();

	@Test
	@DisplayName("Teste do POST do Controller do Carta Objetivo")
	public void testCriacaoCartaObjetivo() throws Exception {
		CartaObjetivo newCartaObjetivo = CartaObjetivo.builder()
			.id(UUID.randomUUID())
			.tipoContagem(3)
			.tipo("")
			.categoria("")
			.textoRegra("Granhe 4 pontos")
			.textoTematico("Teste de POST do Controller")
			.pontos(1)
			.build();

		ObjectMapper mapper = new ObjectMapper();

		String newCartaObjetivoAsJSON = mapper.writeValueAsString(newCartaObjetivo);

		this.mockMvc.perform(post("/cartaobjetivo")
			.content(newCartaObjetivoAsJSON)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated());
	}
	@Test
	@DisplayName("Teste do POST do Controller do Carta Objetivo")
	public void naoDeveCriarCartaObjetivo(){
		when(bindingResult.hasErrors()).thenReturn(true);

		ResponseEntity<CartaObjetivo> cartaObjetivo = cartaObjetivoController.saveCartaObjetivo(newCartaObjetivo, bindingResult);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, cartaObjetivo.getStatusCode());

	}

	@Test
	@DisplayName("Teste do GET do Controller da Carta Objetivo")
	public void deveRetornarSucesso_QuandoBuscarPorId() throws Exception {

		given(cartaObjetivoService.findById(newCartaObjetivo.getId())).willReturn(Optional.of(newCartaObjetivo));

		ObjectMapper mapper = new ObjectMapper();
		String cartaComoJSON = mapper.writeValueAsString(newCartaObjetivo);

		this.mockMvc.perform(get("/cartaobjetivo/" + newCartaObjetivo.getId())
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(cartaComoJSON));
	}
	@Test
	@DisplayName("Teste do GET do Controller da Carta Objetivo")
	public void deveRetornarSucesso_QuandoBuscarTodos() throws Exception {
		List<CartaObjetivo> cartaObjetivoList = new ArrayList<>();
		cartaObjetivoList.add(newCartaObjetivo);
		given(cartaObjetivoService.findAll()).willReturn(cartaObjetivoList);

		ObjectMapper mapper = new ObjectMapper();
		String cartaComoJSON = mapper.writeValueAsString(cartaObjetivoList);

		this.mockMvc.perform(get("/cartaobjetivo")
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(cartaComoJSON));
	}
}