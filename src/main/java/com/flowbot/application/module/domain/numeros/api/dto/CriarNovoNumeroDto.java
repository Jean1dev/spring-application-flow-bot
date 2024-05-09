package com.flowbot.application.module.domain.numeros.api.dto;

public record CriarNovoNumeroDto(
        String nick,
        String numero,
        String whatsappId
) {
    public CriarNovoNumeroDto(String nick, String numero) {
        this(nick, numero, null);
    }
}
