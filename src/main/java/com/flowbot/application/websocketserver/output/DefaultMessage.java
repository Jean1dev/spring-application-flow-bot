package com.flowbot.application.websocketserver.output;

import com.flowbot.application.websocketserver.TypeMessageSocketOutput;

import static com.flowbot.application.websocketserver.TypeMessageSocketOutput.SIMPLE_MESSAGE;

public record DefaultMessage(
        TypeMessageSocketOutput typeMessageSocketOutput,
        String message
) {
    public DefaultMessage(String message) {
        this(SIMPLE_MESSAGE, message);
    }
}
