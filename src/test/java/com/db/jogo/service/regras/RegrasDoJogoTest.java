package com.db.jogo.service.regras;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.UUID;

import com.db.jogo.enums.StatusEnum;
import com.db.jogo.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.Jogador;

class RegrasDoJogoTest {
	
	private CartaDoJogo carta;	
	private Jogador jogador;
	private CartaInicio cartaInicio = new CartaInicio();
    private Baralho baralho = new Baralho();
    private CartaObjetivo cartaObjetivo = new CartaObjetivo();
    private Sala sala = new Sala();
	
	@BeforeEach 
	void criaCarta () {
		carta = CartaDoJogo.builder()
				.valorCoracaoGrande(1)
				.valorCoracaoPequeno(2)
				.build();
		
		 jogador = Jogador.builder()
				.id(UUID.randomUUID())
				.nome("joão")
				.bonusCoracaoGrande(1)
				.bonusCoracaoPequeno(1)
				.coracaoGrande(1)
				.pontos(1)
				.coracaoPequeno(2)
				.build();
	}

	@BeforeEach
	public void init(){
        cartaInicio.setId(UUID.randomUUID());
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");

        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setTexto_tematico("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setTexto_regra("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        baralho.setId(UUID.randomUUID());
        baralho.setCodigo("LILA");
        baralho.setTitulo("Teste");
        baralho.setDescricao("Exemplo");
        baralho.adicionarCartaDoInicio(cartaInicio);
        baralho.setCartasDoJogo(new ArrayList<>());
        baralho.adicionarCartadoJogo(carta);
        baralho.setCartasObjetivo(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);

        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
    }

	@Test
	@DisplayName("Teste jogador com oito pontos, status do jogo ULTIMA_JOGADA")
	void testeValidaFinalPartida() {
		jogador.setPontos(8);
		RegrasDoJogo.defineEstadoFinalPartida(jogador, sala);
		assertEquals("ULTIMA_RODADA", sala.getStatus().name());
	}
	
	@Test
	@DisplayName("Teste jogador com menos de oito pontos, status do jogo NOVO")
	void testeValidaAntesFinalPartida() {
		jogador.setPontos(6);
		RegrasDoJogo.defineEstadoFinalPartida(jogador, sala);
		assertEquals("NOVO", sala.getStatus().name());
	}

	@Test
	@DisplayName("Teste desconta corações ao comprar CartaDoJogo")
	void testDescontaCoracoesCartaDoJogo() {
		jogador.setBonusCoracaoGrande(0);
		jogador.setBonusCoracaoPequeno(0);		
		RegrasDoJogo.descontaCoracoesCartaDoJogo(jogador, carta);
		assertEquals(0, jogador.getCoracaoPequeno());
		assertEquals(0, jogador.getCoracaoGrande());
	}

	@Test
	@DisplayName("Teste valida compra CartaDoJogo VERDADEIRO")
	void testValidaCoracoesVerdadeiro() {
		
			boolean valida =  
				RegrasDoJogo.validaCompraCarta(jogador, this.carta);
		assertEquals(valida, true);
	}

	@Test
	@DisplayName("Teste valida compra CartaDoJogo FALSO")
	void testValidaCoracoesFalso() {
		jogador.setBonusCoracaoGrande(0);
		jogador.setBonusCoracaoPequeno(0);
		jogador.setCoracaoPequeno(1);	
			
			boolean valida =  
				RegrasDoJogo.validaCompraCarta(jogador, carta);
		assertEquals(valida, false);
	}

	@Test
	@DisplayName("Teste adiciona coracoes pequenos")
	void testAdicionaCoracoesPequenos() {
		jogador.setBonusCoracaoPequeno(0);
		jogador.setCoracaoPequeno(0);
		RegrasDoJogo.adicionaCoracoesPequenos(jogador);
		assertEquals(2, jogador.getCoracaoPequeno());
	}

	@Test
	@DisplayName("Teste adiciona coracoes grandes")
	void testAdicionaCoracoesGrandes() {
		jogador.setBonusCoracaoGrande(0);
		jogador.setCoracaoGrande(0);
		RegrasDoJogo.adicionaCoracoesGrandes(jogador);
		assertEquals(1, jogador.getCoracaoGrande());
	}

	@Test
	@DisplayName("Teste Compra CartaObjetivo coracoes VERDADEIRO")
	void testValidaCompraCartaObjetivoVerdadeiro() {
		
		boolean valida =  
				RegrasDoJogo.validaCompraCartaObjetivoCoracaoGrande(jogador);
		assertEquals(valida, true);
	}
	
	@Test
	@DisplayName("Teste Compra CartaObjetivo coracoes FALSO")
	void testValidaCompraCartaObjetivoFalso() {
		jogador.setBonusCoracaoGrande(0);
		jogador.setBonusCoracaoPequeno(0);
		jogador.setCoracaoPequeno(0);	
		jogador.setCoracaoGrande(0);
		boolean validaCoracaoGrande =  
				RegrasDoJogo.validaCompraCartaObjetivoCoracaoGrande(jogador);
		boolean validaCoracaoPequeno = RegrasDoJogo.validaCompraCartaObjetivoCoracaoPequeno(jogador);
		assertEquals(validaCoracaoGrande, false);
		assertEquals(validaCoracaoPequeno, false);

	}
}
