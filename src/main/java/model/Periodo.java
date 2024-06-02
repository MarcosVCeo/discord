package model;

import java.util.Arrays;

public enum Periodo {
    MANHA("Manhã"), TARDE("Tarde"), NOITE("Noite");

    public final String descricao;

    Periodo(String descricao) {
        this.descricao = descricao;
    }

    public static Periodo get(String descricao) {
        return Arrays.stream(Periodo.values())
                .filter(p -> p.descricao.equals(descricao))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi encontrado o periodo: " + descricao));
    }
}
