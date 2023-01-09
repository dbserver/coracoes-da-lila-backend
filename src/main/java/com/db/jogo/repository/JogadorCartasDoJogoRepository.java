package com.db.jogo.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.db.jogo.model.JogadorCartasDoJogo;

public interface JogadorCartasDoJogoRepository extends CrudRepository<JogadorCartasDoJogo, UUID>{    
    JogadorCartasDoJogo findByJogadorIDAndCartaDoJogoID(UUID jogadorID, UUID cartaID);
}