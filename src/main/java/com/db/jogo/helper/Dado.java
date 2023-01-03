package com.db.jogo.helper;

import java.util.Random;

import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Dado {


	public Jogador  girarDado(CartaDoJogo cartaDoJogo, Jogador jogador, Sala sala) {
		Jogador jogadorAtualizado = new Jogador();

		Random ran = new Random();
		if (cartaDoJogo.getBonus()) {
			sala.setDado(ran.nextInt(6) + 1);
			jogadorAtualizado = resultadoDoDado( cartaDoJogo.getTipo(), sala.getDado(), jogador);
		}

		

		return jogadorAtualizado;
	}

	private   static void descontaCoracao(Jogador jogador, Integer bonusCoracao) {


		Integer totalCoracoes = quantidaDeCoracoes(jogador);

		switch (bonusCoracao) {
			case -1: {
				if (jogador.getBonusCoracaoPequeno() > 0) {
					jogador.setBonusCoracaoPequeno(jogador.getBonusCoracaoPequeno() - 1);

				} else if (jogador.getBonusCoracaoGrande() > 0) {
					jogador.setBonusCoracaoGrande(jogador.getBonusCoracaoGrande() - 1);
				}
				break;
			}

			case 1: {
				if (totalCoracoes < 5) {
					jogador.setBonusCoracaoPequeno(jogador.getBonusCoracaoPequeno() + 1);
				}
				break;
			}

			case 2: {
				if (totalCoracoes < 5) {
					jogador.setBonusCoracaoGrande(jogador.getBonusCoracaoGrande() + 1);
				}
				break;
			}
		}
	}

	public static Jogador resultadoDoDado(CartaDoJogoEnumTipo tipo, Integer dado, Jogador jogador) {

		switch (tipo) {
			case ACAO: {
				 if(dado==1){
					 descontaCoracao(jogador, -1);
					return jogador;
				 }else if (dado<=3) {

			return jogador;
				 }else if (dado==4) {
					 descontaCoracao(jogador, 2);
				return jogador;
				}
				else if(dado>=5){
					 descontaCoracao(jogador, 1);
					return jogador;
				}
			}
			
			case INFORMACAO:{
				if(dado<=4){
				return jogador;
				}
				else if(dado>=5){
					descontaCoracao(jogador, 1);
					return jogador;
				}
			}
		}
			return jogador;
	}

	 public static Integer quantidaDeCoracoes( Jogador jogador){
				return (jogador.getBonusCoracaoGrande() + jogador.getCoracaoGrande() + jogador.getBonusCoracaoPequeno() + jogador.getCoracaoPequeno() );

	}
}