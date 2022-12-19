package com.db.jogo.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.db.jogo.enums.CartaDoJogoEnumTipo;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
@SpringBootTest
public class DadoTest {
	
	@MockBean
	private Dado dado;
	
    @Test
    public void verificaResultadoDoDado() throws Exception {

        Jogador jogador = new Jogador();
        jogador.setBonusCoracaoPequeno(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setCoracaoPequeno(2);
        jogador.setCoracaoGrande(0);
        
        
        CartaDoJogo	carta = CartaDoJogo.builder()
    			.bonus(true)
    			.categoria("Ação")
    			.fonte("")
    			.pontos(0)
    			.valorCoracaoGrande(0)
    			.valorCoracaoPequeno(0)
    			.tipo(CartaDoJogoEnumTipo.ACAO)
    			.build();
        
      
        
        Sala sala = Sala.builder()
        		.hash("IZHW")
        		.dado(0)
        		.build();
        
       
       when(dado.girarDado(carta, jogador, sala ))
          .thenReturn(jogador);

       assertThat(dado.girarDado(carta, jogador, sala)).isNotNull();
       
        assertEquals(jogador, dado.girarDado(carta, jogador, sala));

    }
}
