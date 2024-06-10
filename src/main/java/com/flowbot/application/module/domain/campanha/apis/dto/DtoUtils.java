package com.flowbot.application.module.domain.campanha.apis.dto;

import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.numeros.Numero;

public final class DtoUtils {

    public static CampanhaOutput toOutput(Campanha campanha) {
        return new CampanhaOutput(
                campanha.getId(),
                campanha.getTitulo(),
                campanha.getStatus().name(),
                "sem numero"
        );
    }

    public static CampanhaOutput toOutput(Campanha campanha, Numero numero) {
        return new CampanhaOutput(
                campanha.getId(),
                campanha.getTitulo(),
                campanha.getStatus().name(),
                numero.getNumero()
        );
    }
}
