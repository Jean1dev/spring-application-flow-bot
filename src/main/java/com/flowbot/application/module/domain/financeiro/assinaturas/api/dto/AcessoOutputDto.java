package com.flowbot.application.module.domain.financeiro.assinaturas.api.dto;

import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;

public record AcessoOutputDto(
        boolean firstAccess,
        PlanoAtivoOutput plano
) {
}
