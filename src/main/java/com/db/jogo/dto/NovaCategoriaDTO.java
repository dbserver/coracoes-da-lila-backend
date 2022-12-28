package com.db.jogo.dto;

import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;

import lombok.Data;

@Data
public class NovaCategoriaDTO {
    private UUID jogadorID;
    private UUID cartaModificadaID;
    private CartaDoJogoEnumCategoria novaCategoria;
    private String hashDaSala;
}
