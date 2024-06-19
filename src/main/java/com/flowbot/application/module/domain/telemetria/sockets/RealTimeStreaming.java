package com.flowbot.application.module.domain.telemetria.sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.telemetria.TelemetriaRepository;
import com.flowbot.application.websocketserver.output.DefaultMessage;
import com.flowbot.application.websocketserver.output.TelemetriaWhatsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.flowbot.application.websocketserver.WebSocketMessageUtils.createMessage;

@Component
@EnableAsync
public class RealTimeStreaming {

    private static final Logger log = LoggerFactory.getLogger(RealTimeStreaming.class);
    private static final long MAX_THREADS_PER_USER = 3;
    private final NumeroMongoDbRepository numeroMongoDbRepository;
    private final BotBuilderApi botBuilderApi;
    private final TelemetriaRepository telemetriaRepository;

    public RealTimeStreaming(
            NumeroMongoDbRepository numeroMongoDbRepository,
            BotBuilderApi botBuilderApi,
            TelemetriaRepository telemetriaRepository
    ) {
        this.numeroMongoDbRepository = numeroMongoDbRepository;
        this.botBuilderApi = botBuilderApi;
        this.telemetriaRepository = telemetriaRepository;
    }

    @Async
    public void addSubscriber(WebSocketSession session) {
        var currentTenant = TenantThreads.getTenantId();
        numeroMongoDbRepository.findAll()
                .parallelStream()
                .filter(numero -> StatusNumero.VALIDADO.equals(numero.getStatusNumero()))
                .limit(MAX_THREADS_PER_USER)
                .forEach(numero -> streaming(numero, session, currentTenant));
    }

    private void streaming(Numero numero, WebSocketSession session, String currentTenant) {
        synchronized (session) {
            try {
                TextMessage message = createMessage(new DefaultMessage("Buscando dados do numero %s".formatted(numero.getNumero())));
                session.sendMessage(message);
            } catch (IOException e) {
                log.error("Error sending message");
            }
        }

        while (session.isOpen()) {
            log.info("Sending message to session: {}", session.getId());
            try {
                Thread.sleep(2000);

                consultarAsync(numero, session, currentTenant);

            } catch (InterruptedException e) {
                log.error("Error sleeping");
            }
        }
    }

    private void consultarAsync(Numero numero, WebSocketSession session, String currentTenant) {
        CompletableFuture.supplyAsync(() -> botBuilderApi.audit(numero.getWhatsappInternalId()))
                .thenAccept(response ->
                        response
                                .parallelStream()
                                .forEach(auditMessagesResponse -> {
                                    try {
                                        var output = new TelemetriaWhatsMessage(
                                                numero.getId(),
                                                auditMessagesResponse.status(),
                                                auditMessagesResponse.id(),
                                                auditMessagesResponse.remoteJid()
                                        );

                                        telemetriaRepository.save(output, currentTenant);
                                        TextMessage message = createMessage(output);
                                        session.sendMessage(message);
                                    } catch (JsonProcessingException e) {
                                        log.error("Error parsing message");
                                    } catch (IOException e) {
                                        log.error("Error sending message");
                                    }
                                })
                );
    }
}
