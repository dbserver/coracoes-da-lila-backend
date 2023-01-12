package com.db.jogo.model;

import com.db.jogo.enums.StatusEnumJogador;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "jogador")
public class Jogador {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@Column(name = "posicao", length = 10, nullable = false)
	private Integer posicao;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "jogador_cartadojogo", joinColumns = {
			@JoinColumn(name = "jogador_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "cartadojogo_id", referencedColumnName = "id") })
	@Builder.Default
	private List<CartaDoJogo> cartasDoJogo = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "jogador_cartaobjetivo", joinColumns = {
			@JoinColumn(name = "jogador_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "cartaobjetivo_id", referencedColumnName = "id") })
	@Builder.Default
	private List<CartaObjetivo> cartasObjetivo = new ArrayList<>();

	@Column(name = "nome", length = 30, nullable = false)
	private String nome;

	@Column(name = "pontos", length = 20, nullable = false)
	private Integer pontos;

	@Column(name = "coracaoPeq", length = 20, nullable = false)
	private Integer coracaoPequeno;

	@Column(name = "coracaoGra", length = 20, nullable = false)
	private Integer coracaoGrande;

	@Column(name = "bonusCoracaoPeq", length = 10, nullable = false)
	private Integer bonusCoracaoPequeno;

	@Column(name = "bonusCoracaoGra", length = 10, nullable = false)
	private Integer bonusCoracaoGrande;

	@Column(name="is_host", nullable = false)
     private Boolean isHost;

	@Column(name="pontos_objetivo", length = 10)
	private Integer pontosObjetivo;
	
	@Column(name = "status")
	@Builder.Default
	private StatusEnumJogador status = StatusEnumJogador.ESPERANDO ;


	public void adicionaCarta(CartaDoJogo carta) {
		this.cartasDoJogo.add(carta);
	}

	public void removeCarta(CartaDoJogo carta) {
		this.cartasDoJogo.remove(carta);
	}

	public void adicionaObjetivo(CartaObjetivo carta) {
		this.cartasObjetivo.add(carta);
	}

	public void removeObjetivo(CartaObjetivo carta) {
		this.cartasObjetivo.remove(carta);
	}

	@NonNull
	public StatusEnumJogador getStatus() {
		return this.status;
	}

	public void setStatus(@NonNull StatusEnumJogador status) {

		this.status = status;
	}
}

