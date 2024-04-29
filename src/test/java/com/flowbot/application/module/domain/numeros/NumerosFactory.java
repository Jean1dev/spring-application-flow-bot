package com.flowbot.application.module.domain.numeros;

public final class NumerosFactory {

    public static Numero umNumero() {
        return new Numero(
                null,
                "nick",
                null,
                null,
                "numero",
                null
        );
    }

    public static Numero umNumero(String nick) {
        return new Numero(
                null,
                nick,
                null,
                null,
                "numero",
                null
        );
    }
}
