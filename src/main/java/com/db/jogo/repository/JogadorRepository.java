package com.db.jogo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.db.jogo.model.Jogador;

public interface JogadorRepository extends JpaRepository<Jogador, UUID> {

}
