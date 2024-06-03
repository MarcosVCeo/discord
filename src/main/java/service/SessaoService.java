package service;

import model.Periodo;
import model.Sessao;
import org.bson.types.ObjectId;
import repository.SessaoRepository;
import service.exception.SessaoJaCadastradaException;

import java.time.LocalDate;
import java.util.List;
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

    public Optional<Sessao> buscarSessao(ObjectId id) {
        return sessaoRepository.buscarSessao(id);
    }

    public List<Sessao> buscarSessoes(Long idMestre, LocalDate dataInicio, LocalDate dataFim) {
        return sessaoRepository.buscarSessoes(idMestre, dataInicio, dataFim);
    }

    public List<Sessao> buscarSessoesLivres(Long idMestre, LocalDate dataInicio, LocalDate dataFim) {
        return sessaoRepository.buscarSessoesLivres(idMestre, dataInicio, dataFim);
    }

    public void atualizar(Sessao sessao) {
        sessaoRepository.atualizar(sessao);
    }
}
