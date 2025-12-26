package com.flowbot.application.module.domain.usuario;

public final class ConfiguracaoUsuarioFactory {

    public static ConfiguracaoUsuario umaConfiguracao() {
        return new ConfiguracaoUsuario(
                null,
                "https://example.com/logo.png",
                "Nome da Empresa",
                null
        );
    }

    public static ConfiguracaoUsuario umaConfiguracao(String logoUrl, String name) {
        return new ConfiguracaoUsuario(
                null,
                logoUrl,
                name,
                null
        );
    }
}

