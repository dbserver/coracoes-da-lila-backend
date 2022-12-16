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

@Table(name="carta_objetivo")
public class CartaObjetivo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;


	@Column(name = "texto_regra", length = 255, nullable = false)
	private String texto_regra;

	@Column(name = "pontos", length = 10, nullable = false)
	private Integer pontos;

	@Column(name = "categoria", length = 80, nullable = true)
	private String categoria;

	@Column(name = "texto_tematico", length = 255, nullable = false)
	private String texto_tematico;

	@Column(name = "tipo_contagem", length = 10, nullable = false)
	private Integer tipo_contagem;

	@Column(name = "tipo", length = 20, nullable = true)
	private String tipo;

}
