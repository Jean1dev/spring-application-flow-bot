package com.flowbot.application.websocketserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketGateway extends AbstractWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketGateway.class);
    private final Map<String, WebSocketSession> sessions;

    public WebSocketGateway() {
        sessions = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal principal = session.getPrincipal();
        super.afterConnectionEstablished(session);
        log.info("New session connected: {}", session.getId());
        log.info("Principal: {}", principal);
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("Session closed: {}", session.getId());
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }
}
