package com.flowbot.application.module.domain.numeros;

public final class NumerosFactory {

    public static Numero umNumero() {
        return new Numero(
                null,
                "nick",
                null,
                null,
                "numero",
                "whatsappInternalId"
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

    public static Numero umNumero(StatusNumero statusNumero) {
        return new Numero(
                null,
                "nick",
                null,
                statusNumero,
                "numero",
                "null"
        );
    }
}
