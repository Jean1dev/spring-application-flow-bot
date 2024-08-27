package com.flowbot.application.module.db;

import com.flowbot.application.E2ETests;
import com.flowbot.application.context.MultiTenantMongoDatabaseFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class MultipleDatabaseTest extends E2ETests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("adminMongoTemplate")
    private MongoTemplate adminMongoTemplate;
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    public void testMultipleDatabases() {
        Assertions.assertEquals(MultiTenantMongoDatabaseFactory.DEFAULT_DATABASE_NAME + "-", mongoTemplate.getDb().getName());
        Assertions.assertEquals(MultiTenantMongoDatabaseFactory.ADMIN_DATABASE_NAME, adminMongoTemplate.getDb().getName());

        var contentAsSaveInMongoTemplate = DummyObject.of("mongoTemplate");
        var contentAsSaveInAdminMongoTemplate = DummyObject.of("adminMongoTemplate");

        mongoTemplate.save(contentAsSaveInMongoTemplate, "content");
        adminMongoTemplate.save(contentAsSaveInAdminMongoTemplate, "content");

        mongoTemplate.findAll(DummyObject.class, "content")
                .forEach(item -> Assertions.assertEquals(contentAsSaveInMongoTemplate.value, item.value));

        adminMongoTemplate.findAll(DummyObject.class, "content")
                .forEach(item -> Assertions.assertEquals(contentAsSaveInAdminMongoTemplate.value, item.value));
    }

    static class DummyObject {
        public String value;

        public static DummyObject of(String value) {
            var dummyObject = new DummyObject();
            dummyObject.value = value;
            return dummyObject;
        }
    }
}
