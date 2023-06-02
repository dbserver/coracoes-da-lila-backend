package com.db.jogo.factory;

import com.db.jogo.enums.StatusEnumJogador;
import com.db.jogo.model.Jogador;

public class JogadorFactory {   

    private JogadorFactory() {}

    public static Jogador criarPrimeiroJogador(Jogador jogador) {
        jogador.setBonusCoracaoPequeno(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(0);
        jogador.setPontos(0);
        jogador.setPosicao(1);
        jogador.setIsHost(true);
        jogador.setNome(jogador.getNome());
        jogador.setPontosObjetivo(0);
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        return jogador;
    }

    public static Jogador criarJogador(Jogador jogador, Integer num) {
        jogador.setBonusCoracaoPequeno(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(0);
        jogador.setPontos(0);
        jogador.setPosicao(num);
        jogador.setIsHost(false);
        jogador.setNome(jogador.getNome());
        jogador.setPontosObjetivo(0);
        jogador.setStatus(StatusEnumJogador.ESPERANDO);
        return jogador;
    }

}
