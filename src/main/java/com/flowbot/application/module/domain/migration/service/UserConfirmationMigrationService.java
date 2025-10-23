package com.flowbot.application.module.domain.migration.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
        final var collectionName = "user_confirmations";
        var documents = cryptoMongoTemplate.findAll(Document.class, collectionName);
        if (documents.isEmpty()) {
            return 0L;
        }

        adminMongoTemplate.insert(documents, collectionName);
        return documents.size();
    }
}
