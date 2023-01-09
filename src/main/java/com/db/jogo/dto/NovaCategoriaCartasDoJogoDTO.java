package com.db.jogo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class NovaCategoriaCartasDoJogoDTO {

    private UUID jogadorID;
    private String salaHash;
    private List<NovaCategoriaDTO> listaDeCartas = new ArrayList<>();
}
