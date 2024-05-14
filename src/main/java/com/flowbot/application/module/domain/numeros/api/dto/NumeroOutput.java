package com.flowbot.application.module.domain.numeros.api.dto;

public record NumeroOutput(
        String id,
        String nick,
        String numero,
        String status,
        String createdAt
) {
}
