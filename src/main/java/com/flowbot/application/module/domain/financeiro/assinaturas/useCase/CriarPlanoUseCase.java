package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.module.domain.financeiro.assinaturas.PeriodoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CriarPlanoUseCase {
    private final MongoTemplate mongoTemplate;

    public CriarPlanoUseCase(@Qualifier("adminMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String criarPlanoSimples(String email, PeriodoPlano periodoPlano) {
        return mongoTemplate.save(Plano.criarPlanoPadrao(email, periodoPlano)).getId();
    }
}
