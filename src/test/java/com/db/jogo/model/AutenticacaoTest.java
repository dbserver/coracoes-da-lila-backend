package com.db.jogo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AutenticacaoTest {
    Autenticacao autenticacao;

    @BeforeEach
    void setUp() {
        autenticacao = new Autenticacao();
    }

    @Test
    void testaQueEstaLogado() {
        autenticacao.setLogado(true);
        boolean logado = autenticacao.isLogado();
        assertTrue(logado);
    }
    @Test
    void testaQueNaoEstaLogado() {
        autenticacao.setLogado(false);
        boolean logado = autenticacao.isLogado();
        assertFalse(logado);
    }
}