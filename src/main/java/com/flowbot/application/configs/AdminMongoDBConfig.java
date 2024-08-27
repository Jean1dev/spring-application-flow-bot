package com.flowbot.application.configs;

import com.flowbot.application.context.MultiTenantMongoDatabaseFactory;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AdminMongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${mongodb.admin.uri}")
    private String connectionString;

    @Override
    protected String getDatabaseName() {
        return MultiTenantMongoDatabaseFactory.ADMIN_DATABASE_NAME;
    }

    @Bean(name = "adminMongoTemplate")
    public MongoTemplate adminMongoTemplate() {
        return new MongoTemplate(MongoClients.create(connectionString), getDatabaseName());
    }
}
