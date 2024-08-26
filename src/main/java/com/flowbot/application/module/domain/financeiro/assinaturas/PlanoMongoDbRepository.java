package com.flowbot.application.module.domain.financeiro.assinaturas;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanoMongoDbRepository extends MongoRepository<Plano, String> {
}
