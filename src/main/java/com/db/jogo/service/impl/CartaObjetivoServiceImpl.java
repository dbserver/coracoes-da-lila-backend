package com.db.jogo.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.service.CartaObjetivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.repository.CartaObjetivoRepository;

@Service
public class CartaObjetivoServiceImpl implements CartaObjetivoService {

	private final CartaObjetivoRepository cartaObjetivoRepository;

	@Autowired
	public CartaObjetivoServiceImpl(CartaObjetivoRepository cartaObjetivoRepository) {
		this.cartaObjetivoRepository = cartaObjetivoRepository;
	}

	@Override
	public Optional<CartaObjetivo> findById(UUID id) throws DataAccessException {
		return cartaObjetivoRepository.findById(id);
	}

	@Override
	public List<CartaObjetivo> findAll() throws DataAccessException {
		return (List<CartaObjetivo>) cartaObjetivoRepository.findAll();
	}

	@Override
	public CartaObjetivo saveCartaObjetivo(CartaObjetivo cartaObjetivo) throws DataAccessException {
		return cartaObjetivoRepository.save(cartaObjetivo);
	}

}