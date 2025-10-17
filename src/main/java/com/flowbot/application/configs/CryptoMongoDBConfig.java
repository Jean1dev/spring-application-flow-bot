package com.flowbot.application.configs;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class CryptoMongoDBConfig {

    @Value("${mongodb.admin.uri}")
    private String connectionString;

    @Bean(name = "cryptoMongoTemplate")
    public MongoTemplate cryptoMongoTemplate() {
        return new MongoTemplate(MongoClients.create(connectionString), "crypto");
    }
}
