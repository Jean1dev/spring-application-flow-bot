package com.flowbot.application.module.domain.telemetria;

import java.util.List;

public record TelemetriaOutput(
        String numeroId,
        List<TelemetriaRegistroOutput> registros
) {
}
