package com.db.jogo.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "jogador_cartasdojogo")
public class JogadorCartasDoJogo {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

    @Column(name = "jogador_id", nullable = false)
    private UUID idJogador;

    @Column(name = "cartadojogo_id", nullable = false)
	private UUID idCartaDoJogo;

    @Column(name = "nova_categoria")
    private String novaCategoria;

    public JogadorCartasDoJogo(Jogador jogador, CartaDoJogo cartaDoJogo){
        this.idJogador = jogador.getId();
        this.idCartaDoJogo = cartaDoJogo.getId();
    }
}
