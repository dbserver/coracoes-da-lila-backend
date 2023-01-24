package com.db.jogo.service.impl;

import com.db.jogo.model.CartaInicio;
import com.db.jogo.repository.CartaInicioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CartaInicioServiceImplTest {

    @Mock
    CartaInicioRepository cartaInicioRepositoryMock;

    @InjectMocks
    CartaInicioServiceImpl cartaInicioServiceImpl;

    private List<CartaInicio> cartaInicioLista;
    private Iterable<CartaInicio> cartaInicioIterable;
    private CartaInicio cartaInicio;

    @BeforeEach
    void setUp() {
        cartaInicio = CartaInicio.builder().id(UUID.randomUUID())
                .nome("nome")
                .descricao("descricao")
                .build();
        cartaInicioLista = new ArrayList<>();
        cartaInicioLista.add(cartaInicio);

        cartaInicioIterable = cartaInicioLista;
    }

    @Test
    void findAllIterable() {
        when(cartaInicioRepositoryMock.findAll()).thenReturn(cartaInicioIterable);
        Iterable<CartaInicio> cartaInicioFindAll = cartaInicioServiceImpl.findAll();
        Assertions.assertEquals(cartaInicioIterable,cartaInicioFindAll);
    }
}