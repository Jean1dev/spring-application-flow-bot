package com.flowbot.application.websocketserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowbot.application.module.domain.telemetria.sockets.RealTimeStreaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static com.flowbot.application.shared.AuthUtils.setTenant;

@Component
public class WebSocketGateway extends AbstractWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketGateway.class);
    private final Map<String, WebSocketSession> sessions;
    private final RealTimeStreaming realTimeStreaming;
    private final ObjectMapper mapper;

    public WebSocketGateway(RealTimeStreaming realTimeStreaming, ObjectMapper mapper) {
        this.realTimeStreaming = realTimeStreaming;
        this.mapper = mapper;
        sessions = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // Eh uma gambiarra, refac in the future
        var tenantAuth = extractTenantAuth(session);
        //var tenantSeted = setTenant(tenantAuth);
        //log.info("New session connected: {}", tenantSeted);

        super.afterConnectionEstablished(session);
        sessions.put(session.getId(), session);
    }

    private String extractTenantAuth(WebSocketSession session) {
        var sub = session.getUri().getQuery().split("=")[1];
        return sub
                .replace("-", "")
                .replace(".", "");
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
        JsonNode jsonNode = mapper.readTree(message.getPayload());
        log.info("Received message: {}", jsonNode);
        // {type: "join/say", data: "name/msg"}

        switch (jsonNode.get("type").asText()) {
            case "SUBSCRIBE_NUMBERS" -> {
                realTimeStreaming.addSubscriber(session);
            }
            case "say" -> {
                log.info("Saying: {}", jsonNode.get("data").asText());
            }
        }
    }
}
