package com.flowbot.application.module.domain.telemetria;

public record TelemetriaRegistroOutput(
        String status,
        String id,
        String remoteJid
) {
}
