package com.flowbot.application.http.dtos;

public record AuditMessagesResponse(
        String status,
        String id,
        String remoteJid
) {
}
