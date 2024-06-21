package com.flowbot.application.module.domain.telemetria;

import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.websocketserver.output.TelemetriaWhatsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class TelemetriaRepository {

    private static final Logger log = LoggerFactory.getLogger(TelemetriaRepository.class);
    private final MongoTemplate mongoTemplate;
    private static final String COLLECTION_NAME = "telemetria";

    public TelemetriaRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Async
    public void save(TelemetriaWhatsMessage telemetria, String currentTenant) {
        log.info("Saving telemetria: {}", telemetria);
        TenantThreads.setTenantId(currentTenant);
        mongoTemplate.save(telemetria, COLLECTION_NAME);
    }

    public List<TelemetriaOutput> get() {
        var all = mongoTemplate.findAll(TelemetriaWhatsMessage.class, COLLECTION_NAME);
        var elementsToReturn = new ArrayList<TelemetriaOutput>();

        all.stream()
                .collect(Collectors.groupingBy(TelemetriaWhatsMessage::numeroId))
                .forEach((numeroId, telemetrias) -> {
                    var registros = telemetrias.stream()
                            .map(telemetria -> new TelemetriaRegistroOutput(telemetria.status(), telemetria.id(), telemetria.remoteJid()))
                            .collect(Collectors.toList());
                    var telemetriaOutput = new TelemetriaOutput(numeroId, registros);

                    elementsToReturn.add(telemetriaOutput);
                });

        return elementsToReturn;
    }

    public void removeById(String id) {
        var telemetria = mongoTemplate.findById(id, TelemetriaWhatsMessage.class, COLLECTION_NAME);
        if (telemetria != null) {
            log.info("Removing telemetria by id: {}", id);
            mongoTemplate.remove(telemetria, COLLECTION_NAME);
        }
    }

    public void removeAllByNumber(String numberId) {
        log.info("Removing all telemetria by number: {}", numberId);
        mongoTemplate.findAll(TelemetriaWhatsMessage.class, COLLECTION_NAME)
                .stream()
                .filter(telemetria -> telemetria.numeroId().equals(numberId))
                .forEach(telemetria -> mongoTemplate.remove(telemetria, COLLECTION_NAME));

    }
}
