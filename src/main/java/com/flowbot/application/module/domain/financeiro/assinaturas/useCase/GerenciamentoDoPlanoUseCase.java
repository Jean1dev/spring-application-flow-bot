package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class GerenciamentoDoPlanoUseCase {
    private final MongoTemplate mongoTemplate;

    public GerenciamentoDoPlanoUseCase(@Qualifier("adminMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PlanoAtivoOutput obterDadosPlano(String email) {
        return mongoTemplate.findAll(Plano.class)
                .stream()
                .filter(plano -> plano.getUsuario().email().equals(email))
                .findFirst()
                .map(PlanoAtivoOutput::map)
                .orElseThrow();
    }
}
