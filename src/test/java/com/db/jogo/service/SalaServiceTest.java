package com.db.jogo.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaInicio;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;


@ExtendWith(MockitoExtension.class)
public class SalaServiceTest {

    @Mock
    private SalaService salaService;

    @Mock
    private SalaService salaVazia;

    CartaInicio cartaInicio = new CartaInicio();
    Baralho baralho = new Baralho();
    CartaDoJogo carta = new CartaDoJogo();
    CartaObjetivo cartaObjetivo = new CartaObjetivo();
    Jogador jogador = new Jogador();
    Jogador jogador2 = new Jogador();
    Sala sala = new Sala();


    @BeforeEach
    public void init(){
        cartaInicio.setId(UUID.randomUUID());
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");

        carta.setId(UUID.randomUUID());
        carta.setPontos(2);
        carta.setBonus(true);
        carta.setCategoria("Visual");
        carta.setTexto("Deficiencia visual");
        carta.setFonte("Wikipedia");
        carta.setValorCoracaoGrande(0);
        carta.setValorCoracaoPequeno(0);
        carta.setTipo("Ação");

        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setDescricao("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setClassificacao("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        baralho.setId(UUID.randomUUID());
        baralho.setCodigo("LILA");
        baralho.setTitulo("Teste");
        baralho.setDescricao("Exemplo");
        baralho.setCartasInicio(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);
        baralho.setCartasDoJogo(new ArrayList<>());
        baralho.adicionarCartadoJogo(carta);
        baralho.setCartasObjetivo(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);

        //jogador 1
        jogador.setId(UUID.randomUUID());
        jogador.setNome("Felipe");
        jogador.setPontos(0);
        jogador.setBonusCoracaoGrande(0);
        jogador.setBonusCoracaoPequeno(0);
        jogador.setCoracaoGrande(0);
        jogador.setCoracaoPequeno(0);
        jogador.setCartasDoJogo(new ArrayList<>());
        jogador.adicionaCarta(carta);
        jogador.adicionaObjetivo(cartaObjetivo);

        //jogador 2
        jogador2.setId(UUID.randomUUID());
        jogador2.setNome("Igor");
        jogador2.setPontos(1);
        jogador2.setBonusCoracaoGrande(2);
        jogador2.setBonusCoracaoPequeno(1);
        jogador2.setCoracaoGrande(1);
        jogador2.setCoracaoPequeno(1);
        jogador2.setCartasDoJogo(new ArrayList<>());
        jogador2.adicionaCarta(carta);
        jogador2.adicionaObjetivo(cartaObjetivo);


        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");

        sala.setStatus(Sala.StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
        sala.adicionarJogador(jogador2);
    }



    Optional<Sala> salaLocalizada;


    @Test
    @DisplayName("Teste para encontrar uma sala por Hash")
    void encontrarSalaPorHash() {
        salaLocalizada = salaService.findSalaByHash("iuervnijr0f");
        assertEquals(salaLocalizada, salaService.findSalaByHash("iuervnijr0f"));
    }

     @DisplayName("Teste para criar uma sala do Service")
     @Test
     void criarSala(){
         when(salaService.saveSala(sala)).thenReturn(sala);;
         assertEquals(sala, salaService.saveSala(sala));
     }

     @DisplayName("Teste de erro do retorno da sala")
     @Test
     void encontrarSalaPorHashComErro() {
         salaLocalizada = salaService.findSalaByHash("ertfvygbhnj");
         assertFalse(salaLocalizada.isPresent());
     }
 
     @DisplayName("Teste de erro do SAVE do Service")
     @Test
      void criarSalaComErro(){
         when(salaService.saveSala(null)).thenReturn(null);
         assertNull(salaService.saveSala(null));
      }

    @Test
    @DisplayName("Teste de número de jogadores na sala")
    void testarNumeroJogadores() {
        when(salaService.totalJogadores("ertfvygbhnj")).thenReturn(2);
        assertEquals(2, salaService.totalJogadores("ertfvygbhnj"));
    }

    @Test
    @DisplayName("Teste de quem é o primeiro jogador (host)")
    void testarPrimeiroJogador() {
        when(salaService.findFirst("ertfvygbhnj")).thenReturn(jogador);
        assertEquals(jogador, salaService.findFirst("ertfvygbhnj"));
    }

    @Test
    @DisplayName("Testa quem é o primeiro jogador quando a sala está vazia")
    void testaPrimeiroEmSalaVazia() {
        when(salaVazia.findFirst(sala.getHash())).thenReturn(jogador);
        assertEquals(jogador, salaVazia.findFirst(sala.getHash()));
    }

    @Test
    @DisplayName("Teste de encontrar sala vazia")
    void testaSalaNula() {
        when(salaService.findFirst(null)).thenReturn(null);
        assertNull(salaService.findFirst(null));
    }

}
