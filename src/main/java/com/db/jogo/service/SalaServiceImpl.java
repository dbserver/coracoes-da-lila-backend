package com.db.jogo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
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
	public Optional<Sala> updateSala(Sala sala) {
		Optional<Sala> salaToUpdate = salaRepository.findSalaByHash(sala.getHash());

		if (salaToUpdate.isPresent()) {
			salaToUpdate.get().setBaralho(sala.getBaralho());
			salaToUpdate.get().setDado(sala.getDado());
			salaToUpdate.get().setJogadores(sala.getJogadores());
			salaToUpdate.get().setStatus(sala.getStatus());
		}
		return salaToUpdate;
	}
}
