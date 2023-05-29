package com.db.jogo.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.db.jogo.enums.StatusCartaDoJogoEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "sala_cartadojogo")
public class SalaCartaDoJogo {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

    @OneToOne
	@JoinColumn(name = "cartadojogo_id")
    private CartaDoJogo cartaDoJogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    @JsonIgnore
    private Sala sala;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusCartaDoJogoEnum status;

}
