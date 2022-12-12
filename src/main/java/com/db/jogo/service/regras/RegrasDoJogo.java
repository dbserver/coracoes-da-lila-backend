package com.db.jogo.service.regras;

import com.db.jogo.enums.StatusEnum;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.helper.Dado;

public class RegrasDoJogo {
	
	public static void defineEstadoFinalPartida(Jogador jogador, Sala sala) {
        if(jogador.getPontos() >= 8) {
            sala.setStatus(StatusEnum.ULTIMA_RODADA);
        }
    }

    public static Jogador descontaCoracoesCartaDoJogo(Jogador jogador, CartaDoJogo carta) {
    	
    	int numCoracoesGraDaCarta = carta.getValorCoracaoGrande();
		int numCoracoesPeqDaCarta = carta.getValorCoracaoPequeno();
		
		if(jogador.getBonusCoracaoPequeno() > 0) {
			numCoracoesPeqDaCarta -= jogador.getBonusCoracaoPequeno();
		}
		if(jogador.getBonusCoracaoGrande() > 0) {
			numCoracoesGraDaCarta -= jogador.getBonusCoracaoGrande();
		}
		if(numCoracoesGraDaCarta > 0) {
			jogador.setCoracaoGrande(jogador.getCoracaoGrande() - numCoracoesGraDaCarta);
		}
		if(numCoracoesPeqDaCarta > 0) {
			jogador.setCoracaoPequeno(jogador.getCoracaoPequeno() - numCoracoesPeqDaCarta);
		}
    	return jogador ;
    }

	public static Jogador descontaCoracaoPequenoCartaObjetivo(Jogador jogador) {
    	
		if (jogador.getBonusCoracaoPequeno() > 0){
			return jogador;
		}
		if (jogador.getCoracaoPequeno() > 0){
			jogador.setCoracaoPequeno(jogador.getCoracaoPequeno() - 1);
		}
		return jogador;
	}

	public static Jogador descontaCoracaoGrande(Jogador jogador) {
		if (jogador.getBonusCoracaoGrande() > 0){
			return jogador;
		}
		if (jogador.getCoracaoGrande() > 0){
			jogador.setCoracaoGrande((jogador.getCoracaoGrande() - 1));
		}
		return jogador;
	}
	
    public static boolean validaCompraCarta(Jogador jogador, CartaDoJogo carta) {

		if (carta.getValorCoracaoPequeno() >= 0) {
			if (jogador.getBonusCoracaoPequeno() + jogador.getCoracaoPequeno() < carta.getValorCoracaoPequeno()) {
				return false;
			}

		}

		if (carta.getValorCoracaoGrande() >= 0) {
			if (jogador.getBonusCoracaoGrande() + jogador.getCoracaoGrande() < carta.getValorCoracaoGrande()) {
				return false;
			}

		}

			return true;
	}
	
	public static Jogador adicionaCoracoesPequenos (Jogador jogador) {
	
		 int coracaoPequenouenos = 2;
		 
		 if(Dado.quantidaDeCoracoes(jogador) < 4) {
			
			jogador.setCoracaoPequeno(coracaoPequenouenos+= jogador.getCoracaoPequeno());

			 						 
		 }
			
		
		return jogador;
		
	}

	public static Jogador adicionaCoracoesGrandes (Jogador jogador) {
		
		 int coracaoGrandendes = 1;
		 
		 if(Dado.quantidaDeCoracoes(jogador) < 5) {
			 
			 jogador.setCoracaoGrande(coracaoGrandendes += jogador.getCoracaoGrande());
		 }
			
		return jogador;
		
	}

	public static boolean validaCompraCartaObjetivoCoracaoPequeno(Jogador jogador) {
		if (jogador.getBonusCoracaoPequeno() + jogador.getCoracaoPequeno() < 1) {
			return false;
		}

		return true;
	}

	public static boolean validaCompraCartaObjetivoCoracaoGrande(Jogador jogador) {

		if (jogador.getBonusCoracaoGrande() + jogador.getCoracaoGrande() < 1) {
				return false;
			}
		return true;

	}
	
}


