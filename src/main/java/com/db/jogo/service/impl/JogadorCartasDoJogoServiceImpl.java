package com.db.jogo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.db.jogo.model.JogadorCartasDoJogo;
import com.db.jogo.repository.JogadorCartasDoJogoRepository;
import com.db.jogo.service.JogadorCartasDoJogoService;

@Service
public class JogadorCartasDoJogoServiceImpl implements JogadorCartasDoJogoService{
    
    private final JogadorCartasDoJogoRepository jogadorCartasDoJogoRepository;

    @Autowired
	public JogadorCartasDoJogoServiceImpl(JogadorCartasDoJogoRepository jogadorCartasDoJogoRepository) {
		this.jogadorCartasDoJogoRepository = jogadorCartasDoJogoRepository;
	}

    @Override
    public JogadorCartasDoJogo saveJogadorCartasDoJogo(JogadorCartasDoJogo jogadorCartasDoJogo)
            throws DataAccessException {
        return jogadorCartasDoJogoRepository.save(jogadorCartasDoJogo);
    }
}
