package repository;

import model.Periodo;
import model.Sessao;

import java.time.LocalDate;
import java.util.Optional;

public interface SessaoRepository {
    Optional<Sessao> buscarSessao(Long idMestre, LocalDate data, Periodo periodo);

    void agendarSessao(Sessao sessao);
}
