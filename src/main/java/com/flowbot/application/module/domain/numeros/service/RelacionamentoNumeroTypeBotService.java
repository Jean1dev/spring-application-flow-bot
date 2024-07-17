package com.flowbot.application.module.domain.numeros.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelacionamentoNumeroTypeBotService {
    private final MongoTemplate mongoTemplate;
    public static final String COLLECTION_NAME = "relacionamento_numero_typebot";

    public RelacionamentoNumeroTypeBotService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long quantidadeDeNumerosVinculadosNoTypebot(String typeboName, String apiHost) {
        return mongoTemplate.count(new Query()
                        .addCriteria(Criteria.where("name").is(typeboName))
                        .addCriteria(Criteria.where("apiHost").is(apiHost)),
                COLLECTION_NAME);
    }

    public void relacionar(String numeroId, String typeboName, String apiHost) {
        removerRelacionamentosAntigos(numeroId, typeboName, apiHost);

        mongoTemplate.save(
                new RelacionamentoNumeroTypeBot(numeroId, typeboName, apiHost),
                COLLECTION_NAME);
    }

    private void removerRelacionamentosAntigos(String numeroId, String typeboName, String apiHost) {
        buscarRelacionamentos(numeroId)
                .stream()
                .filter(it -> it.name().equals(typeboName) && it.apiHost().equals(apiHost))
                .findFirst()
                .ifPresent(it -> {
                    mongoTemplate.remove(new Query()
                                    .addCriteria(Criteria.where("numeroId").is(numeroId))
                                    .addCriteria(Criteria.where("name").is(typeboName))
                                    .addCriteria(Criteria.where("apiHost").is(apiHost)),
                            COLLECTION_NAME);
                });
    }

    private List<RelacionamentoNumeroTypeBot> buscarRelacionamentos(String numeroId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("numeroId").is(numeroId));
        return mongoTemplate.find(query, RelacionamentoNumeroTypeBot.class, COLLECTION_NAME);
    }

    public record RelacionamentoNumeroTypeBot(
            String numeroId,
            String name,
            String apiHost
    ) {
    }
}
