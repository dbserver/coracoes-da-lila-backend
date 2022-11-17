package com.db.jogo.service.regras;

import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.model.Sala.StatusEnum;
import com.db.jogo.helper.Dado;

public class RegrasDoJogo {
	
	public static void setaStatusFinalPartida(Jogador jogador, Sala sala) {
        if(jogador.getPontos() >= 8) {
            sala.setStatus(StatusEnum.ULTIMA_RODADA);
        }
    }
	


    public static Jogador descontaCoracoes(Jogador jogador, CartaDoJogo carta) {
    	
    	int numCoracoesGraDaCarta = carta.getValorCorGrande();
		int numCoracoesPeqDaCarta = carta.getValorCoracaoPequeno();
		
		if(jogador.getBonusCoracaoPeq() > 0) {
			numCoracoesPeqDaCarta -= jogador.getBonusCoracaoPeq();
		}
		if(jogador.getBonusCoracaoGra() > 0) {
			numCoracoesGraDaCarta -= jogador.getBonusCoracaoGra();
		}
		if(numCoracoesGraDaCarta > 0) {
			jogador.setCoracaoGra(jogador.getCoracaoGra() - numCoracoesGraDaCarta);
		}
		if(numCoracoesPeqDaCarta > 0) {
			jogador.setCoracaoPeq(jogador.getCoracaoPeq() - numCoracoesPeqDaCarta);
		}
    	return jogador ;
    }
    
	public static boolean validaCompraCarta(Jogador jogador, CartaDoJogo carta) {

		if (carta.getValorCoracaoPequeno() >= 0) {
			if (jogador.getBonusCoracaoPeq() + jogador.getCoracaoPeq() < carta.getValorCoracaoPequeno()) {
				return false;
			}

		}

		if (carta.getValorCorGrande() >= 0) {
			if (jogador.getBonusCoracaoGra() + jogador.getCoracaoGra() < carta.getValorCorGrande()) {
				return false;
			}

		}

			return true;
	}
	

	public static Jogador adicionaCoracoesPequenos (Jogador jogador) {
	
		 int coracaoPequenos = 2;
		 
		 if(Dado.quantidaDeCoracoes(jogador) < 4) {
			
			jogador.setCoracaoPeq(coracaoPequenos+= jogador.getCoracaoPeq());

			 						 
		 }
			
		
		return jogador;
		
	}
	

	
	// metodo para coracoes grande 
	
	public static Jogador adicionaCoracoesGrandes (Jogador jogador) {
		
		 int coracaoGrandes = 1;
		 
		 if(Dado.quantidaDeCoracoes(jogador) < 5) {
			 
			 jogador.setCoracaoGra(coracaoGrandes += jogador.getCoracaoGra());
		 }
			
		
		return jogador;
		
	}

	
	public static boolean validaCompraCartaObjetivo(Jogador jogador) {

		if (jogador.getBonusCoracaoPeq() + jogador.getCoracaoPeq() + jogador.getBonusCoracaoGra() + jogador.getCoracaoGra() < 1) {
				return false;
			}

		return true;

	}
	
}


