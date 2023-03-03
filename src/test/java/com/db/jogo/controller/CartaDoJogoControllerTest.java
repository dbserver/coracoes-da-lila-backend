package com.db.jogo.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.service.impl.CartaDoJogoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.BindingResult;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CartaDoJogoController.class)
@DisplayName("Carta Do Jogo Controller Teste")
public class CartaDoJogoControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	CartaDoJogoServiceImpl cartaDoJogoService;
	@Mock
	BindingResult bindingResult;
	@InjectMocks
	CartaDoJogoController cartaDoJogoController;

	CartaDoJogo cartaDoJogoVazia = CartaDoJogo.builder().valorCoracaoGrande(null).build();

	String id = "d1516d33-ff6f-4dc9-aedf-9316421096cb";
	CartaDoJogo newCartaDoJogo = CartaDoJogo.builder()
							.id(UUID.fromString(id))
							.bonus(true)
							.valorCoracaoGrande(1)
							.valorCoracaoPequeno(0)
							.categoria(CartaDoJogoEnumCategoria.FISICA)
							.fonte("Google").tipo(CartaDoJogoEnumTipo.INFORMACAO)
							.pontos(3)
							.texto("Teste")
							.build();

	@Test
	@DisplayName("Teste do POST/Sucesso do Controller Carta do Jogo")
	public void deveRetornarSucesso_QuandoCriarCartaDoJogo() throws Exception {

		when(cartaDoJogoService.saveCartaDoJogo(any(CartaDoJogo.class))).thenReturn(newCartaDoJogo);

		ObjectMapper mapper = new ObjectMapper();
		String cartaDoJogoJSON = mapper.writeValueAsString(newCartaDoJogo);

		mockMvc.perform(MockMvcRequestBuilders.post("/cartadojogo").content(cartaDoJogoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.content().json(cartaDoJogoJSON));
	}
	@Test
	@DisplayName("Teste do POST/Sucesso do Controller Carta do Jogo")
	public void deveRetornarBadRequest_QuandoCriarCartaDoJogo() throws Exception {

		when(bindingResult.hasErrors()).thenReturn(true);

		ResponseEntity<CartaDoJogo> cartaDoJogoResponseEntity = cartaDoJogoController.salvarCartaDoJogo(cartaDoJogoVazia, bindingResult);
		assertEquals(HttpStatus.BAD_REQUEST, cartaDoJogoResponseEntity.getStatusCode());

	}
	@Test
	void deveListarTodasCartaDoJogo() throws Exception {
		List<CartaDoJogo> cartaDoJogos = new ArrayList<>();
		cartaDoJogos.add(newCartaDoJogo);
		when(cartaDoJogoService.findAll()).thenReturn(cartaDoJogos);

		ObjectMapper mapper = new ObjectMapper();
		String listaCartaInicioComoJSON = mapper.writeValueAsString(cartaDoJogos);

		mockMvc.perform(get("/cartadojogo").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().json(listaCartaInicioComoJSON));
	}
	@Test
	void deveRetornarNotFoundAoProcurarCartaDoJogo() throws Exception {
		mockMvc.perform(get("/cartadojogo").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound());
	}
	@Test
	void deveRetornarNotFoundAoProcurarCartaDoJogoPorId() throws Exception {
		UUID valor = UUID.fromString("b2615428-9608-11ed-a1eb-0242ac120002");
		when(cartaDoJogoService.findById(cartaDoJogoVazia.getId())).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/cartadojogo/" + valor).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

	}

	@Test
	@DisplayName("Teste do POST/Error do Controller da Carta Do Jogo")
	public void deveRetornarErro_QuandoCriarCartaDoJogoInvalido() throws Exception {

		when(cartaDoJogoService.saveCartaDoJogo(any(CartaDoJogo.class))).thenReturn(newCartaDoJogo);

		mockMvc.perform(MockMvcRequestBuilders.post("/cartadojogo").accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());

	}

	@Test
	@DisplayName("Teste do GET do Controller Carta Do jogo")
	public void deveRetornarSucesso_QuandoBuscarCartaDoJogo() throws Exception {

		given(cartaDoJogoService.findById(newCartaDoJogo.getId())).willReturn(Optional.of(newCartaDoJogo));

		ObjectMapper mapper = new ObjectMapper();
		String cartaDoJogoJSON = mapper.writeValueAsString(newCartaDoJogo);

		mockMvc.perform(get("/cartadojogo/" + newCartaDoJogo.getId()).content(cartaDoJogoJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().json(cartaDoJogoJSON));

	}
}