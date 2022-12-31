package com.db.jogo.dto;

import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;

import lombok.Data;

@Data
public class NovaCategoriaDTO {

    private UUID cartaID;
    private CartaDoJogoEnumCategoria novaCategoria;
}
