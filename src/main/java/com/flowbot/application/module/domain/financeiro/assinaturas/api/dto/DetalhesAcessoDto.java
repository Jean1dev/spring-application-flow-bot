package com.flowbot.application.module.domain.financeiro.assinaturas.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record DetalhesAcessoDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataAcesso,
        String origem,
        String localizacao
) {
}
