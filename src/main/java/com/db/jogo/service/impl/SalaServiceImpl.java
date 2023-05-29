package com.db.jogo.service.impl;

import java.util.Arrays;
import java.util.Collections;

import java.util.List;
import java.util.Optional;

import java.util.UUID;
import java.util.stream.Collectors;

import com.db.jogo.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.db.jogo.enums.StatusCartaDoJogoEnum;
import com.db.jogo.enums.StatusEnum;
import com.db.jogo.exception.JogoInvalidoException;
import com.db.jogo.model.Baralho;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.model.SalaCartaDoJogo;
import com.db.jogo.repository.SalaRepository;

@Service
public class SalaServiceImpl implements SalaService {

	private final SalaRepository salaRepository;

	@Autowired
	public SalaServiceImpl(SalaRepository salaRepository) {
		this.salaRepository = salaRepository;
	}

	@Override
	public Optional<Sala> findSalaByHash(String hash) throws DataAccessException {
		return salaRepository.findSalaByHash(hash);
	}

	@Override
	public Sala saveSala(Sala sala) throws DataAccessException {
		return salaRepository.save(sala);
	}

	/**
	 * Este método executa a jogada
	 * 
	 * @param sala - essa sala é o estado inicial do jogo
	 * @return retorna um novo estado do jogo após a jogada computada
	 * @throws DataAccessException
	 */
	@Override
	public Sala jogada(Sala sala) throws DataAccessException {

		return sala;
	}

	@Override
	public Integer totalJogadores(String hash) {
		Optional<Sala> optsala = salaRepository.findSalaByHash(hash);
		if (optsala.isEmpty()) {
			return 0;
		}
		Sala sala = optsala.get();
		List<Jogador> lista = sala.getJogadores();
		return lista.size();
	}

	@Override
	public Jogador findFirst(String hash) {
		Optional<Sala> sala = salaRepository.findSalaByHash(hash);
		List<Jogador> lista = sala.get().getJogadores();
		if (lista.isEmpty()) {
			return null;
		}
		return lista.get(0);
	}

	@Override
	public Sala criaSala(Jogador jogador, Baralho baralho) throws JogoInvalidoException {		
		if (jogador == null || jogador.getNome().isEmpty()) {
            throw new JogoInvalidoException("Nome do jogador deve ser informado");
        }
		if (baralho == null) {
			throw new JogoInvalidoException("Baralho deve ser informado");
		}

		Sala sala = new Sala();
        sala.setId(UUID.randomUUID());
        sala.setHash(sala.generateHash());
        sala.setJogadores(Arrays.asList(jogador));
        sala.setJogadorEscolhido(jogador); // TODO verificar pq eh inicializadp        
        sala.setDado(0);
        sala.setStatus(StatusEnum.AGUARDANDO);

		sala.sorteiaCartaInicial(baralho.getCartasInicio());	

		List<SalaCartaDoJogo> cartasDoJogo = baralho.getCartasDoJogo().stream().map(carta ->  
			new SalaCartaDoJogo(UUID.randomUUID(), carta, sala, StatusCartaDoJogoEnum.PILHA))
				.collect(Collectors.toList());
		Collections.shuffle(cartasDoJogo); 
		sala.setCartasDoJogo(cartasDoJogo);		

		Collections.shuffle(baralho.getCartasObjetivo()); 
		sala.setCartasObjetivo(baralho.getCartasObjetivo());

		return saveSala(sala);
	}
	
}