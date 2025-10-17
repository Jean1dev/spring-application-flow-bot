package com.flowbot.application.module.domain.migration.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConfirmationMigrationService {

    private final MongoTemplate cryptoMongoTemplate;
    private final MongoTemplate adminMongoTemplate;

    public UserConfirmationMigrationService(
            @Qualifier("cryptoMongoTemplate") MongoTemplate cryptoMongoTemplate,
            @Qualifier("adminMongoTemplate") MongoTemplate adminMongoTemplate) {
        this.cryptoMongoTemplate = cryptoMongoTemplate;
        this.adminMongoTemplate = adminMongoTemplate;
    }

    public long migrateUserConfirmations() {
        List<Document> userConfirmations = cryptoMongoTemplate.findAll(Document.class, "user_confirmations");

        if (userConfirmations.isEmpty()) {
            return 0;
        }

        adminMongoTemplate.insertAll(userConfirmations);

        return userConfirmations.size();
    }
}
