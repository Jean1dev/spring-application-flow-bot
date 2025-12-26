package com.flowbot.application.module.domain.usuario.apis.dto;

import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;

import java.time.LocalDateTime;

public record ConfiguracaoUsuarioOutput(
        String id,
        String logoUrl,
        String name,
        LocalDateTime dataCriacao
) {
    public static ConfiguracaoUsuarioOutput from(ConfiguracaoUsuario configuracao) {
        return new ConfiguracaoUsuarioOutput(
                configuracao.getId(),
                configuracao.getLogoUrl(),
                configuracao.getName(),
                configuracao.getDataCriacao()
        );
    }
}

