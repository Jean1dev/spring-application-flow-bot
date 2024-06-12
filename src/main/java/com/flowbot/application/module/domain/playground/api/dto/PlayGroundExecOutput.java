package com.flowbot.application.module.domain.playground.api.dto;

public record PlayGroundExecOutput(
        boolean success,
        boolean needValidadeNumber,
        String message,
        String senderId
) {
}
