package com.flowbot.application.module.domain.usuario.apis.dto;

import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;

public record ConfiguracaoUsuarioPublicaDto(
        String logoUrl,
        String name
) {
    public static ConfiguracaoUsuarioPublicaDto from(ConfiguracaoUsuario configuracao) {
        return new ConfiguracaoUsuarioPublicaDto(
                configuracao.getLogoUrl(),
                configuracao.getName()
        );
    }
}

