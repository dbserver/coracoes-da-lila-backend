package com.db.jogo.controller;

import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.db.jogo.model.Admin;
import com.db.jogo.model.Autenticacao;
import com.db.jogo.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.validation.BindingResult;

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(LoginController.class)
@DisplayName("Login Controller Teste")
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private LoginService loginService;
	@Mock
	BindingResult bindingResult;
	@InjectMocks
	LoginController loginController;

	@Test
	public void testVerificaSenhaSucesso() throws Exception {
		
		Admin admin = new Admin();
		admin.setId(UUID.randomUUID());
		admin.setSenha("123");
		
		given(loginService.verificaSenha("123")).willReturn(true);

		this.mockMvc
				.perform(post("/login").content(asJsonString(admin)).accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.logado").value("true")).andExpect(status().isOk());
	}

	@Test
	public void testVerificaSenhaInvalida() throws Exception {
		Admin adminRequest = new Admin();
		adminRequest.setSenha("321");

		this.mockMvc.perform(post("/login")
						.content(asJsonString(adminRequest)).accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(jsonPath("$.logado").value("false"))
				.andExpect(status().isOk());
	}

	@Test
	public void deveRetornarBadRequestQuandoTiverErros(){
		Admin admin = new Admin();
		admin.setId(UUID.randomUUID());
		admin.setSenha("123");

		when(bindingResult.hasErrors()).thenReturn(true);

		ResponseEntity<Autenticacao> verificaSenha = loginController.verificaSenha(admin, bindingResult);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, verificaSenha.getStatusCode());
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
