package com.db.jogo.model;

import com.db.jogo.enums.StatusEnum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SalaTest {
    Sala sala = new Sala();

    @Test
    @DisplayName("Teste para ver se a variavel DataHoraFimDoJogo está sendo gravada")
    void getDataHoraFimDeJogo() {
        sala.setStatus(StatusEnum.FINALIZADO);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDataHoraFimDoJogo()), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())));
        assertNotNull(StatusEnum.FINALIZADO);
    };

    @Test
    @DisplayName("Teste para ver se a Tdh_Inicio está sendo gravada")
    void getDth_inicio() {
        assertEquals(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(sala.getDth_inicio()), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Timestamp.from(Instant.now())));
        assertNotNull(sala.getDth_inicio());
    };
    
    @Test
    @DisplayName("Teste para verificar se os jogadores estão sendo trocados")
    void trocaJogadores(){
        Jogador jogador = new Jogador();
        Jogador jogadorTeste = new Jogador();
        
        jogador.setId(UUID.randomUUID());
        jogador.setNome("Felipe");
        jogador.setPontos(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setBonusCoracaoPequeno(0);
        jogador.setCoracaoGrande(0);
        jogador.setCoracaoPequeno(0);
        jogador.setPosicao(1);
        
        jogadorTeste.setId(UUID.randomUUID());
        jogadorTeste.setNome("Guilherme");
        jogadorTeste.setPontos(0);
        jogadorTeste.setBonusCoracaoGrande(0);
        jogadorTeste.setBonusCoracaoPequeno(0);
        jogadorTeste.setCoracaoGrande(0);
        jogadorTeste.setCoracaoPequeno(0);
        jogadorTeste.setPosicao(2);
        
        List<Jogador> jogadores = new ArrayList<>();;
        jogadores.add(jogador);
        jogadores.add(jogadorTeste);
        
        sala.setJogadores(jogadores);
        sala.mudaPrimeiroJogador(jogadorTeste);
        assertEquals(jogadorTeste,sala.getJogadores().get(0));
    }
};