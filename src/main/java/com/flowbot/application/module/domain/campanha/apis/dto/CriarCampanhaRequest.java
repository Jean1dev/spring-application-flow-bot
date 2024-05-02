package com.flowbot.application.module.domain.campanha.apis.dto;

import java.util.List;

public record CriarCampanhaRequest(
        String titulo,
        String numeroIdRef,
        List<String> numerosParaDisparo,
        String categoria,
        List<String> arquivosUrls
) {
}
