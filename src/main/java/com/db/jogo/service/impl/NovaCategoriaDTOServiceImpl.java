package com.db.jogo.service.impl;

import java.util.UUID;

import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.CartaDoJogoService;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.NovaCategoriaDTOService;
import com.db.jogo.service.SalaService;

public class NovaCategoriaDTOServiceImpl implements NovaCategoriaDTOService{

    private JogadorService jogadorService;
    private SalaService salaService;
    private CartaDoJogoService cartaDoJogoService;

    public Jogador buscarJogadorPorId(UUID jogadorID){
        return this.jogadorService.findById(jogadorID).get();
    }

    public Sala buscarSalaPorHash(String hash){
        return this.salaService.findSalaByHash(hash).get();
    }
    
    public CartaDoJogo buscarCartaDoJogoPorId(UUID cartaID){
        return this.cartaDoJogoService.findById(cartaID).get();
    }
}
