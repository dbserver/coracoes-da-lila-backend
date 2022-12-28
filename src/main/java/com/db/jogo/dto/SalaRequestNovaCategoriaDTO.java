package com.db.jogo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import lombok.Data;

@Data
public class SalaRequestNovaCategoriaDTO {

    private UUID jogadorID;
    private String salaHash;
    private List<NovaCategoriaDTO> listaCartasParaAtualizar = new ArrayList<>();

}
