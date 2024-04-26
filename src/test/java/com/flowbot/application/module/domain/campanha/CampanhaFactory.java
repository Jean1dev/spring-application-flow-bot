package com.flowbot.application.module.domain.campanha;

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
}
