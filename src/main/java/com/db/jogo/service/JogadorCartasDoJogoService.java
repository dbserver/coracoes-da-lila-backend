package com.db.jogo.service;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.db.jogo.model.JogadorCartasDoJogo;

@Service
public interface JogadorCartasDoJogoService {
	
	JogadorCartasDoJogo saveJogadorCartasDoJogo(JogadorCartasDoJogo jogadorCartasDoJogo) throws DataAccessException;	
	JogadorCartasDoJogo findByJogadorIDAndCartaDoJogoID(UUID jogadorId, UUID cartaId);
}
