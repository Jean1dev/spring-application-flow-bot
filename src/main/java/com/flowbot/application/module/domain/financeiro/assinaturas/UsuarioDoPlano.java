package com.flowbot.application.module.domain.financeiro.assinaturas;

public record UsuarioDoPlano(
        String nick,
        String email,
        String referenciaAuthProvider
) {
}
