package com.db.jogo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.JogadorService;
import com.db.jogo.service.SalaService;

import lombok.Data;

@Data
public class SalaRequestNovaCategoriaDTO {

    private UUID jogadorID;
    private String hashDaSala;
    private List<NovaCategoriaDTO> novaCategoriaDTO = new ArrayList<>();

}
