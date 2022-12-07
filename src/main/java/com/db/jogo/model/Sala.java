package com.db.jogo.model;

import com.db.jogo.enums.StatusEnum;
import com.db.jogo.enums.StatusEnumJogador;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name="sala")
public class Sala {

    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Jogador> jogadores;

	@OneToOne
	private Jogador escolhido;

	@OneToOne
	private Baralho baralho;
	
	@NonNull
	@Column(name = "hash" , nullable =false )
	String hash;
    
	@NonNull
	@Column(name="dado" , length =1 , nullable = false)
	private Integer dado;
	
	@NotNull
	@Column(name="status")
	@Builder.Default
	private StatusEnum status = StatusEnum.NOVO;

	public String generateHash() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[6];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		return encoder.encodeToString(bytes);
	}

	@NonNull
	public void adicionarJogador(Jogador jogador) {
		this.jogadores.add(jogador);
	}

	public boolean removerJogador(Jogador jogador) {
		return this.jogadores.remove(jogador);
	}

	@NonNull
	public StatusEnum getStatus() {
		return this.status;
	}

	public void setStatus(@NonNull StatusEnum status) {
		this.status= status;
	}
        
	public void mudaPrimeiroJogador(Jogador escolhido){
		int posicao = escolhido.getPosicao();
		Collections.rotate(this.jogadores, (posicao*-1));
    }
	public Jogador getEscolhido() {
		return this.escolhido;
	}

	public void setEscolhido(Jogador escolhido) {
		this.escolhido= escolhido;
	}
}
