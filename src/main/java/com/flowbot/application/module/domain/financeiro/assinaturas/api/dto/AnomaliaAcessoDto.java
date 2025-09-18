package com.flowbot.application.module.domain.financeiro.assinaturas.api.dto;

import java.util.List;

public record AnomaliaAcessoDto(
        String planoId,
        String emailUsuario,
        int ano,
        int mes,
        int totalAcessos,
        int diasUnicos,
        int origensUnicas,
        int localizacoesUnicas,
        NivelSuspeita nivelSuspeita,
        List<String> motivosDeteccao,
        List<DetalhesAcessoDto> detalhesAcessos
) {
    public enum NivelSuspeita {
        BAIXO, MEDIO, ALTO, CRITICO
    }
}
