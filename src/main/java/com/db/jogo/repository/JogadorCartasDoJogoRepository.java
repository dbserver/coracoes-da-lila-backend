package com.db.jogo.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.db.jogo.model.JogadorCartasDoJogo;

@Repository
public interface JogadorCartasDoJogoRepository extends CrudRepository<JogadorCartasDoJogo, UUID>{
    
}