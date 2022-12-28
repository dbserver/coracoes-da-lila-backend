package com.db.jogo.service;

import java.util.UUID;

import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;

public interface NovaCategoriaDTOService {
    Jogador buscarJogadorPorId(UUID jogadorID);
    Sala buscarSalaPorHash(String hash);
    CartaDoJogo buscarCartaDoJogoPorId(UUID cartaID);
}
