package com.flowbot.application.module.domain.campanha;

import java.util.List;

public final class CampanhaFactory {

    public static Campanha umaCampanha() {
        return new Campanha(
                null,
                "Campanha Teste",
                "Campanha Teste",
                null,
                CategoriaCampanha.AVISO,
                null,
                null
        );
    }

    public static Campanha umaCampanha(List<String> numeros) {
        return new Campanha(
                null,
                "Campanha Teste",
                "Campanha Teste",
                numeros,
                CategoriaCampanha.AVISO,
                null,
                null
        );
    }

    public static Campanha umaCampanha(String idNumero) {
        return new Campanha(
                null,
                "Campanha Teste",
                idNumero,
                List.of("num1", "num2"),
                CategoriaCampanha.AVISO,
                null,
                null
        );
    }
}
