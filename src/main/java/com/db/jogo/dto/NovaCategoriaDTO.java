package com.db.jogo.dto;

import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;

import lombok.Data;

@Data
public class NovaCategoriaDTO {
    public UUID jogadorID;
    public UUID cartaModificadaID;
    public CartaDoJogoEnumCategoria novaCategoria;
    public String hashDaSala;
}
