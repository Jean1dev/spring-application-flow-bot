package com.flowbot.application.module.domain.usuario;

public final class ConfiguracaoUsuarioFactory {

    public static ConfiguracaoUsuario umaConfiguracao() {
        return new ConfiguracaoUsuario(
                null,
                "test-tenant-id",
                "https://example.com/logo.png",
                "Nome da Empresa",
                null
        );
    }

    public static ConfiguracaoUsuario umaConfiguracao(String tenantId) {
        return new ConfiguracaoUsuario(
                null,
                tenantId,
                "https://example.com/logo.png",
                "Nome da Empresa",
                null
        );
    }

    public static ConfiguracaoUsuario umaConfiguracao(String tenantId, String logoUrl, String name) {
        return new ConfiguracaoUsuario(
                null,
                tenantId,
                logoUrl,
                name,
                null
        );
    }
}

