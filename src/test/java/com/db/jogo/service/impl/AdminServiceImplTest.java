package com.db.jogo.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.db.jogo.model.Admin;

import com.db.jogo.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Teste")
class AdminServiceImplTest {

    public static final UUID ID = UUID.fromString("6fa44d23-fac3-4426-b73d-0d0474e29d31");
    public static final String SENHA = "123";
    @Mock
    AdminRepository adminRepositoryMock;

    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    Admin admin;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(ID)
                .senha(SENHA)
                .build();
    }

    @Test
    void deveriaEncontrarAdminPorId() {

        when(adminRepositoryMock.findById(ID)).thenReturn(Optional.of(admin));

        Admin adminEncontradoPorId = adminServiceImpl.findById(ID);

        assertNotNull(adminEncontradoPorId);
        assertEquals(admin, adminEncontradoPorId);
    }
    @Test
    void naoDeveriaEncontrarAdminPorId() {
        when(adminRepositoryMock.findById(ID)).thenReturn(null);

        Admin adminNaoEncontradoPorId = adminServiceImpl.findById(ID);

        assertNull(adminNaoEncontradoPorId);
    }

    @Test
    void deveriaSalvarAdmin() {
        when(adminRepositoryMock.save(admin)).thenReturn(admin);

        Admin adminSalvo = adminServiceImpl.saveAdmin(admin);

        assertNotNull(adminSalvo);
        assertEquals(admin, adminSalvo);
    }

    @Test
    void findAll() {
        Iterable<Admin> adminIterable = List.of(admin);

        when(adminRepositoryMock.findAll()).thenReturn(adminIterable);

        Iterable<Admin> todosAdminsEncontrados = adminServiceImpl.findAll();

        assertNotNull(todosAdminsEncontrados);
        assertEquals(adminIterable, todosAdminsEncontrados);
    }

    @Test
    void deveriaEncontrarAdminPorSenha() {
        when(adminRepositoryMock.findBySenha(SENHA)).thenReturn(admin);

        Admin adminEncontradoPorSenha = adminServiceImpl.findBySenha(SENHA);

        assertNotNull(adminEncontradoPorSenha);
        assertEquals(admin, adminEncontradoPorSenha);
    }

    @Test
    void naoDeveriaEncontrarAdminPorSenha() {
        when(adminRepositoryMock.findBySenha(SENHA)).thenReturn(null);

        Admin adminEncontradoPorSenha = adminServiceImpl.findBySenha(SENHA);

        assertNull(adminEncontradoPorSenha);
    }

}