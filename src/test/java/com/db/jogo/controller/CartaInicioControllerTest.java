package com.db.jogo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.model.CartaInicio;
import com.db.jogo.service.CartaInicioService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.validation.BindingResult;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CartaInicioController.class)
@DisplayName("Carta Inicio Controller Teste")
class CartaInicioControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	CartaInicioService cartaInicioService;
	@Mock
	CartaInicio cartaInicioVazia = CartaInicio.builder()
			.nome(null)
			.descricao(null)
			.build();
	@Mock
	BindingResult bindingResult = null;
	@InjectMocks
	CartaInicioController cartaInicioController;
	String id = "d1516d33-ff6f-4dc9-aedf-9316421096cb";
	CartaInicio newCartaInicio = CartaInicio.builder()
			.nome("testeNome")
			.descricao("testeDescrição")
			.id(UUID.fromString(id))
			.build();

	List<CartaInicio> cartaInicioList;
	Iterable<CartaInicio> cartaInicioIterable;

	@Test
	@DisplayName("Teste do POST/saveCartaInicio deve retornar codigo 201")
	public void testCriacaoCartaInicio() throws Exception {

		when(cartaInicioService.saveCartaInicio(any(CartaInicio.class))).thenReturn(newCartaInicio);
		ObjectMapper mapper = new ObjectMapper();

		String newcartaInicioAsJSON = mapper.writeValueAsString(newCartaInicio);

		this.mockMvc.perform(post("/cartainicio").content(newcartaInicioAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
	}
	@Test
	@DisplayName("Teste do POST/saveCartaInicio deve retornar codigo 400")
	void NãoDeveSalvarCarta(){
		when(bindingResult.hasErrors()).thenReturn(true);

		ResponseEntity<CartaInicio> cartaInicioResponseEntity = cartaInicioController.saveCartaInicio(cartaInicioVazia, bindingResult);
		assertEquals(HttpStatus.BAD_REQUEST,cartaInicioResponseEntity.getStatusCode());
	}

	@Test
	@DisplayName("Teste do GET/procuraListaCarta deve retornar codigo 200")
	public void deveRetornarSucesso_QuandoBuscar() throws Exception {

		ArrayList<CartaInicio> CartaInicio = new ArrayList<>();

		given(cartaInicioService.findAllCartaInicio()).willReturn(CartaInicio);

		ObjectMapper mapper = new ObjectMapper();
		String listaCartaInicioComoJSON = mapper.writeValueAsString(CartaInicio);

		mockMvc.perform(get("/cartainicio/listar")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().json(listaCartaInicioComoJSON));

	}
	
	@Test
	@DisplayName("Teste do GET/procuraCarta deve retornar codigo 200")
	public void deveRetornarSucesso_QuandoBuscarLista() throws Exception {
		given(cartaInicioService.findById(newCartaInicio.getId())).willReturn(Optional.of(newCartaInicio));

		ObjectMapper mapper = new ObjectMapper();

		String cartaInicioJSON = mapper.writeValueAsString(newCartaInicio);
		mockMvc.perform(get("/cartainicio/" + newCartaInicio.getId()).content(cartaInicioJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.content().json(cartaInicioJSON));
	}
	@Test
	@DisplayName("Teste do GET/procuraCarta deve retornar codigo 404")
	public void deveRetornarNotFound_QuandoBuscarLista() throws Exception {
		UUID valor = UUID.fromString("b2615428-9608-11ed-a1eb-0242ac120002");
		when(cartaInicioService.findById(cartaInicioVazia.getId())).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.get("/cartainicio/" + valor).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Teste do GET/findAll deve retornar codigo 200")
	void findCarta() throws Exception {
		cartaInicioList = new ArrayList<>();
		cartaInicioList.add(newCartaInicio);

		cartaInicioIterable = cartaInicioList;
		when(cartaInicioService.findAll()).thenReturn(cartaInicioIterable);

		ObjectMapper mapper = new ObjectMapper();

		String cartaInicioJSON = mapper.writeValueAsString(cartaInicioIterable);
		mockMvc.perform(get("/cartainicio").content(cartaInicioJSON)
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
}