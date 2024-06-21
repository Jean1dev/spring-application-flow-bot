package com.flowbot.application.websocketserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

public final class WebSocketMessageUtils {

    public static TextMessage createMessage(Object payload) throws JsonProcessingException {
        return new TextMessage(new ObjectMapper().writeValueAsString(payload));
    }
}
