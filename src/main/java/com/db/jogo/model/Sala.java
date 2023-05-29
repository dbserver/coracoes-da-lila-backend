package com.db.jogo.model;

import com.db.jogo.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.Base64.Encoder;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "sala")
public class Sala {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Jogador> jogadores;

	@OneToOne
	@JoinColumn(name = "jogador_escolhido")
    private Jogador jogadorEscolhido;

	@OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonIgnore
	public List<SalaCartaDoJogo> cartasDoJogo = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "sala_cartaobjetivo", joinColumns = {
			@JoinColumn(name = "sala_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "cartaobjetivo_id", referencedColumnName = "id") })
	@Builder.Default
	public List<CartaObjetivo> cartasObjetivo = new ArrayList<>();

	@Transient
	@Builder.Default
	public List<CartaObjetivo> opcoesCartaObjetivo = new ArrayList<>();

	@Transient
	@Builder.Default
	public CartaObjetivo cartaObjetivoEscolhida = new CartaObjetivo();

	@Column(name = "carta_inicio_id", nullable =false)
	private UUID cartaInicioId;

	@NonNull
	@Column(name = "hash" , nullable =false)
	String hash;
	
	@NonNull
	@Column(name = "dth_inicio", nullable = false)
	@Builder.Default
	@JsonIgnore
	private Timestamp dth_inicio = dataHoraAtual();
    
	@NonNull
	@Column(name="dado" , length =1 , nullable = false)
	private Integer dado;
		
	@Column(name="dth_fim")
    @JsonIgnore
	private Timestamp dataHoraFimDoJogo;
	
	@NotNull
	@Column(name="status")
	@Builder.Default
	private StatusEnum status = StatusEnum.NOVO;

	@Transient
	@Builder.Default
	public List<CartaDoJogo> cartasDisponiveis = new ArrayList<>();

	public String generateHash() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[6];
		random.nextBytes(bytes);
		Encoder encoder = Base64.getUrlEncoder().withoutPadding();
		return encoder.encodeToString(bytes);
	}

	public void adicionarCartaDoObjetivo(CartaObjetivo cartaObjetivo) {
		this.cartasObjetivo.add(cartaObjetivo);
	}

	public boolean removerCartaDoObjetivo(CartaObjetivo cartaDoObjetivo) {
		return this.cartasObjetivo.remove(cartaDoObjetivo);
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
		this.setDataHoraFimDeJogo();
	}

	public Jogador getJogadorEscolhido() {
        return this.jogadorEscolhido;
    }

	public void setDataHoraFimDeJogo(){
		this.dataHoraFimDoJogo = dataHoraAtual();
	}

	public static Timestamp dataHoraAtual() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
		return Timestamp.from(Instant.now());
	}

	public void sorteiaCartaInicial(List <CartaInicio> cartasInicio){
        Random random = new Random();
        int seletor = random.nextInt(cartasInicio.size());
        this.cartaInicioId = cartasInicio.get(seletor).getId();
    }

}
