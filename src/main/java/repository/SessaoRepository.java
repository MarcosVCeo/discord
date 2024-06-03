package repository;

import model.Periodo;
import model.Sessao;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SessaoRepository {
    Optional<Sessao> buscarSessao(Long idMestre, LocalDate data, Periodo periodo);

    Optional<Sessao> buscarSessao(ObjectId id);

    void agendarSessao(Sessao sessao);

    List<Sessao> buscarSessoes(Long idMestre, LocalDate dataInicio, LocalDate dataFim);

    List<Sessao> buscarSessoesLivres(Long idMestre, LocalDate dataInicio, LocalDate dataFim);

    void atualizar(Sessao sessao);
}
