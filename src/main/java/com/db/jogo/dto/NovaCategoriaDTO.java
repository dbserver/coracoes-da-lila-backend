package com.db.jogo.dto;

import java.util.UUID;

import com.db.jogo.enums.CartaDoJogoEnumCategoria;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.service.CartaDoJogoService;

import lombok.Data;

@Data
public class NovaCategoriaDTO {
    private CartaDoJogoService cartaDoJogoService;

    private UUID cartaModificadaID;
    private CartaDoJogoEnumCategoria novaCategoria;

    public CartaDoJogo buscarCartaDoJogo(){
        return this.cartaDoJogoService.findById(cartaModificadaID).get();
    }
}
