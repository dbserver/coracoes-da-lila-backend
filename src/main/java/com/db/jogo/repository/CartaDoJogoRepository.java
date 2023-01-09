package com.db.jogo.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.db.jogo.model.CartaDoJogo;

public interface CartaDoJogoRepository extends CrudRepository<CartaDoJogo, UUID> {

}