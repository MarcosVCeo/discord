package service;

import model.Periodo;
import model.Sessao;
import repository.SessaoRepository;
import service.exception.SessaoJaCadastradaException;

import java.time.LocalDate;
import java.util.Optional;

public class SessaoService {

    private final SessaoRepository sessaoRepository;

    public SessaoService(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    public boolean possuiSessaoMarcada(Long idMestre, LocalDate data, Periodo perido) {
        return sessaoRepository.buscarSessao(idMestre, data, perido).isPresent();
    }

    public void agendarSessao(Sessao sessao) throws SessaoJaCadastradaException {
        var possuiSessaoMarcada = this.possuiSessaoMarcada(sessao.getMestreId(), sessao.getData(), sessao.getPeriodo());

        if (possuiSessaoMarcada) {
            throw new SessaoJaCadastradaException("Sessão já marcada nesse horário");
        } else {
            sessaoRepository.agendarSessao(sessao);
        }
    }

    public Optional<Sessao> buscarSessao(Long idMestre, LocalDate data, Periodo perido) {
        return sessaoRepository.buscarSessao(idMestre, data, perido);
    }
}
