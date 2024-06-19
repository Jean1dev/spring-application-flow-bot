package com.flowbot.application.websocketserver.output;

import com.flowbot.application.websocketserver.TypeMessageSocketOutput;

public record TelemetriaWhatsMessage(
        TypeMessageSocketOutput type,
        String numeroId,
        String status,
        String id,
        String remoteJid
) {
    public TelemetriaWhatsMessage(String numeroId, String status, String id, String remoteJid) {
        this(TypeMessageSocketOutput.TELEMETRIA_MENSAGEM, numeroId, status, id, remoteJid);
    }
}
