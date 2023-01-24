package com.db.jogo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.db.jogo.model.Baralho;
import com.db.jogo.service.impl.BaralhoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.UUID;

@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(BaralhoController.class)
@DisplayName("Baralho Controller Teste")
class BaralhoControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	BaralhoServiceImpl baralhoService;
	@Mock
	BindingResult bindingResult;
	@InjectMocks
	BaralhoController baralhoController;

	@Test
	@DisplayName("Teste do POST do Controller do Baralho")
	public void testCriacaoBaralho() throws Exception {
		Baralho newBaralho = Baralho.builder().id(UUID.randomUUID()).codigo("LILA1")

				.titulo("Corações de Lila").descricao("Jogo de cartas.").build();

		ObjectMapper mapper = new ObjectMapper();

		String newBaralhoAsJSON = mapper.writeValueAsString(newBaralho);
		this.mockMvc.perform(post("/baralho").content(newBaralhoAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
	}
	@Test
	@DisplayName("Teste do POST do Controller do Baralho")
	public void naoDeveCriarBaralho() throws Exception {
		when(bindingResult.hasErrors()).thenReturn(true);
		Baralho newBaralho = Baralho.builder().id(UUID.randomUUID()).codigo("LILA1")
				.titulo("Corações de Lila").descricao("Jogo de cartas.").build();
		ResponseEntity<Baralho> saveBaralho = baralhoController.saveBaralho(newBaralho, bindingResult);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST, saveBaralho.getStatusCode());

	}

    @Test
    void findBaralho() throws Exception {
		Baralho newBaralho = Baralho.builder().id(UUID.randomUUID()).codigo("LILA1")
				.titulo("Corações de Lila").descricao("Jogo de cartas.").build();
		when(baralhoService.findAll()).thenReturn(List.of(newBaralho));

		ObjectMapper mapper = new ObjectMapper();

		String cartaInicioJSON = mapper.writeValueAsString(newBaralho);
		mockMvc.perform(get("/baralho").content(cartaInicioJSON)
						.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
    }
}
