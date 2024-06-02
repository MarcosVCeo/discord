package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sessao {

    private LocalDate data;
    private Long mestreId;
    private final List<Long> jogadoresIds;
    private Periodo periodo;

    public Sessao(LocalDate data, Long mestreId, Periodo periodo) {
        this.data = data;
        this.mestreId = mestreId;
        this.periodo = periodo;
        this.jogadoresIds = new ArrayList<>();
    }

    public Sessao(LocalDate data, Long mestreId, List<Long> jogadoresIds, Periodo periodo) {
        this.data = data;
        this.mestreId = mestreId;
        this.jogadoresIds = jogadoresIds;
        this.periodo = periodo;
    }

    public LocalDate getData() {
        return data;
    }

    public Long getMestreId() {
        return mestreId;
    }

    public List<Long> getJogadoresIds() {
        return jogadoresIds;
    }

    public Periodo getPeriodo() {
        return periodo;
    }
}
