package com.db.jogo.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.db.jogo.service.JogadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.db.jogo.model.Jogador;
import com.db.jogo.repository.JogadorRepository;

@Service
public class JogadorServiceImpl implements JogadorService {

	private final JogadorRepository jogadorRepository;

	@Autowired
	public JogadorServiceImpl(JogadorRepository jogadorRepository) {
		this.jogadorRepository = jogadorRepository;

	}

	@Override
	public Optional<Jogador> findById(UUID id) throws DataAccessException {
		return jogadorRepository.findById(id);
	}

	@Override
	public Jogador saveJogador(Jogador jogador) throws DataAccessException {
		return jogadorRepository.save(jogador);

	}



	public Optional<Jogador> atualizarJogador(Jogador jogador) throws IllegalArgumentException {

		Optional<Jogador> jogadorParaAtualizar = Optional.empty();

		try {

			jogadorParaAtualizar = jogadorRepository.findById(jogador.getId());

			if (jogadorParaAtualizar.isPresent()) {

				jogadorParaAtualizar.get().setBonusCoracaoGra(jogador.getBonusCoracaoGra());
				jogadorParaAtualizar.get().setBonusCoracaoPeq(jogador.getBonusCoracaoPeq());
				jogadorParaAtualizar.get().setCoracaoGra(jogador.getCoracaoGra());
				jogadorParaAtualizar.get().setCoracaoPeq(jogador.getCoracaoPeq());
				jogadorParaAtualizar.get().setCartasDoJogo(jogador.getCartasDoJogo());
				jogadorParaAtualizar.get().setCartasObjetivo(jogador.getCartasObjetivo());
				jogadorParaAtualizar.get().setNome(jogador.getNome());
				jogadorParaAtualizar.get().setPontos(jogador.getPontos());

				Optional<Jogador> jogadorRetonadoDoSaveDoBanco = Optional.ofNullable(
						jogadorRepository.save(
						jogadorParaAtualizar.get()
						));  
				return jogadorRetonadoDoSaveDoBanco;
				
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Impossível fazer atualização do objeto passado! ", e);
		}
		return jogadorParaAtualizar;
	}

	@Override
	public int totalJogadores() {
		List<Jogador> lista = (List<Jogador>) this.jogadorRepository.findAll();
		if(lista.size() == 0) {
			return 0;
		}
		return lista.size();
	}

	@Override
	public Iterable<Jogador> findAll() {
		return this.jogadorRepository.findAll();
	}

	@Override
	public Boolean podeJogar() {
		int numeroJogadores = this.totalJogadores();
		return numeroJogadores >= 2 && numeroJogadores <= 6;
	}

}
