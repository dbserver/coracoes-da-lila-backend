package com.db.jogo.dto;

import java.util.UUID;


import lombok.Data;

@Data
public class NovaCategoriaDTO {

    private UUID cartaModificadaID;
    private String novaCategoria;
}
