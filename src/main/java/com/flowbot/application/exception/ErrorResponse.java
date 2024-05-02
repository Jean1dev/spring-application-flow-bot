package com.flowbot.application.exception;

import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details
) {
}
