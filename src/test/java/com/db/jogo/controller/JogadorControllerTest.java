package com.db.jogo.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.db.jogo.model.Jogador;
import com.db.jogo.service.impl.JogadorServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.BindingResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Jogador Controller Teste")
class JogadorControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	JogadorServiceImpl jogadorService;
	@Mock
	BindingResult bindingResult;

	String id = "d1516d33-ff6f-4dc9-aedf-9316421096cb";
	Jogador jogador = Jogador.builder()
			.id(UUID.fromString(id))
			.nome("joão")
			.bonusCoracaoGrande(1)
			.bonusCoracaoPequeno(1)
			.coracaoGrande(1)
			.pontos(1)
			.coracaoPequeno(2)
			.build();
	@InjectMocks
	JogadorController jogadorController;

	@Test
	@DisplayName("Teste do POST/Sucesso do Controller do Jogador")
	public void deveRetornarSucesso_QuandoCriarJogador() throws Exception {

		when(jogadorService.saveJogador(any(Jogador.class))).thenReturn(jogador);

		ObjectMapper mapper = new ObjectMapper();
		String jogadorComoJSON = mapper.writeValueAsString(jogador);

		mockMvc.perform(MockMvcRequestBuilders.post("/jogador").content(jogadorComoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated()).andExpect(content().json(jogadorComoJSON));

	}

	@Test
	@DisplayName("Teste do POST/Error do Controller do Jogador")
	public void deveRetornarErro_QuandoCriarJogadorInvalido() throws Exception {

		when(jogadorService.saveJogador(any(Jogador.class))).thenReturn(jogador);

		mockMvc.perform(MockMvcRequestBuilders.post("/jogador").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());

	}
	@Test
	@DisplayName("Teste do POST/Bad_Request do Controller do Jogador")
	public void deveRetornarBadRequest_QuandoCriarJogadorInvalido() throws Exception {

		when(bindingResult.hasErrors()).thenReturn(true);
		ResponseEntity<Jogador> saveJogador = jogadorController.saveJogador(jogador, bindingResult);
		assertEquals(HttpStatus.BAD_REQUEST, saveJogador.getStatusCode());

	}

	@Test
	@DisplayName("Teste do PUT/Sucesso do Controller do Jogador")
	public void deveRetornarSucesso_QuandoAtualizarJogador() throws Exception {

		given(jogadorService.atualizarJogador(jogador)).willReturn(Optional.of(jogador));

		ObjectMapper mapper = new ObjectMapper();
		String jogadorParaAtualizarComoJSON = mapper.writeValueAsString(jogador);

		mockMvc.perform(MockMvcRequestBuilders.put("/jogador").content(jogadorParaAtualizarComoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().json(jogadorParaAtualizarComoJSON));

	}

	@Test
	@DisplayName("Teste do PUT/Error ID NULL do Controller do Jogador")
	public void deveRetornarErro_QuandoAtualizarJogadorIdNull() throws Exception {

		Jogador jogadore = Jogador.builder()
				.id(null)
				.nome("joão")
				.bonusCoracaoGrande(1)
				.bonusCoracaoPequeno(1)
				.coracaoGrande(1)
				.pontos(1)
				.coracaoPequeno(2)
				.build();
		given(jogadorService.atualizarJogador(jogadore)).willReturn(Optional.of(jogadore));

		ObjectMapper mapper = new ObjectMapper();
		String jogadorParaAtualizarComoJSON = mapper.writeValueAsString(jogadore);

		mockMvc.perform(MockMvcRequestBuilders.put("/jogador").content(jogadorParaAtualizarComoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest());


	}

	@Test
	@DisplayName("Teste PUT/Error ID não Encontrado do Controller do Jogador")
	public void deveRetornarErro_QuandoAtualizarJogador() throws Exception {

		Jogador jogadorAtualizar = Jogador.builder()
				.id(UUID.randomUUID())
				.nome("joão")
				.bonusCoracaoGrande(1)
				.bonusCoracaoPequeno(1)
				.coracaoGrande(1)
				.pontos(1)
				.coracaoPequeno(2)
				.build();
		given(jogadorService.atualizarJogador(jogadorAtualizar)).willReturn(Optional.of(jogador));

		ObjectMapper mapper = new ObjectMapper();
		String jogadorParaAtualizarComoJSON = mapper.writeValueAsString(jogador);

		mockMvc.perform(MockMvcRequestBuilders.put("/jogador").content(jogadorParaAtualizarComoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());

	}

	@Test
	@DisplayName("Teste do GET do Controller do Jogador")
	public void deveRetornarSucesso_QuandoBuscar() throws Exception {

		given(jogadorService.findById(jogador.getId())).willReturn(Optional.of(jogador));

		ObjectMapper mapper = new ObjectMapper();
		String jogadorComoJSON = mapper.writeValueAsString(jogador);

		mockMvc.perform(get("/jogador/" + jogador.getId()).content(jogadorComoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().json(jogadorComoJSON));
	}
	@Test
	@DisplayName("Teste do GET do Controller do Jogador")
	public void deveRetornarNotFound_QuandoBuscar() throws Exception {
		String valor = "40fc17ae-9681-11ed-a1eb-0242ac120002";
		mockMvc.perform(get("/jogador/" + valor).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}
	@Test
	@DisplayName("Teste do GET do Controller do Jogador")
	public void deveRetornarTodos_QuandoBuscar() throws Exception {
		List<Jogador> jogadores = new ArrayList<>();
		jogadores.add(jogador);
		when(jogadorService.findAll()).thenReturn(jogadores);

		ObjectMapper mapper = new ObjectMapper();
		String jogadorComoJSON = mapper.writeValueAsString(jogadores);

		mockMvc.perform(get("/jogador/todos").content(jogadorComoJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	@Test
	@DisplayName("Teste do GET do Controller do Jogador")
	public void deveRetornarTotalJogadores_QuandoBuscar() throws Exception {
		List<Jogador> jogadores = new ArrayList<>();
		jogadores.add(jogador);
		when(jogadorService.totalJogadores()).thenReturn(jogadores.size());

		ObjectMapper mapper = new ObjectMapper();
		String jogadorComoJSON = mapper.writeValueAsString(jogadores);

		mockMvc.perform(get("/jogador/totaljogadores").content(jogadorComoJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(content().json("1"));
	}
	@Test
	public void deveRetornarTrueParaPoderJogar() throws Exception {

		when(jogadorService.podeJogar()).thenReturn(true);

		mockMvc.perform(get("/jogador/podeJogar").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());


	}
}
