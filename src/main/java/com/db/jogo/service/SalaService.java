package com.db.jogo.service;

import java.util.Optional;

import org.springframework.dao.DataAccessException;

import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;

public interface SalaService {
    Optional<Sala> findSalaByHash(String hash) throws DataAccessException;
    Sala saveSala(Sala sala) throws DataAccessException;
    Sala jogada(Sala sala) throws DataAccessException;
    Integer totalJogadores(String hash);
    Jogador findFirst(String hash);
    Sala criaSala(Jogador jogador, Baralho baralho) throws JogoInvalidoException;

}
