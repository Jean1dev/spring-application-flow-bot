package com.flowbot.application.module.domain.campanha;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CampanhaMongoDBRepository extends MongoRepository<Campanha, String> {
}
