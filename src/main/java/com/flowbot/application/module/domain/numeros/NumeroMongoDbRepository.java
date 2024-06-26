package com.flowbot.application.module.domain.numeros;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NumeroMongoDbRepository extends MongoRepository<Numero, String> {
    List<Numero> findAllByStatusNumero(StatusNumero statusNumero);
}
