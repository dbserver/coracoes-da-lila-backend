package com.db.jogo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.db.jogo.model.Admin;
import com.db.jogo.service.AdminService;
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
@WebMvcTest(AdminController.class)
@DisplayName("Admin Controller Teste")
class AdminControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	AdminService adminService;
	@Mock
	BindingResult bindingResult;
	@InjectMocks
	AdminController adminController;

	@Test
	@DisplayName("Teste do POST do Controller do Admin")
	public void testCriacaoAdmin() throws Exception {
		Admin newAdmin = Admin.builder().senha("123").id(UUID.randomUUID()).build();

		ObjectMapper mapper = new ObjectMapper();
		String newAdminAsJSON = mapper.writeValueAsString(newAdmin);
		this.mockMvc.perform(post("/admin").content(newAdminAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
	}
	@Test
	public void deveRetornarBadRequestQuandoSalvarAdmin() throws Exception {
		Admin newAdmin = Admin.builder().senha("123").id(UUID.randomUUID()).build();

		when(bindingResult.hasErrors()).thenReturn(true);
		ResponseEntity<Admin> admin = adminController.saveAdmin(newAdmin, bindingResult);
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, admin.getStatusCode());
	}
	@Test
	public void deveRetornarAdmin() throws Exception {
		Admin newAdmin = Admin.builder().senha("123").id(UUID.randomUUID()).build();
		List<Admin> adminList = new ArrayList<>();
		adminList.add(newAdmin);
		when(adminService.findAll()).thenReturn(adminList);

		ObjectMapper mapper = new ObjectMapper();
		String newAdminAsJSON = mapper.writeValueAsString(adminList);
		this.mockMvc.perform(get("/admin").content(newAdminAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());

	}
}