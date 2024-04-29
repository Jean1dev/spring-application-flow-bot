package com.flowbot.application.module.domain.numeros;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NumeroMongoDbRepository extends MongoRepository<Numero, String> {
}
