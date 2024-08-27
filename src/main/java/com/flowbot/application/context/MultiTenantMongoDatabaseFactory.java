package com.flowbot.application.context;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

public class MultiTenantMongoDatabaseFactory extends SimpleMongoClientDatabaseFactory {
    public static final String DEFAULT_DATABASE_NAME = "user";
    public static final String ADMIN_DATABASE_NAME = "flowadmin";

    public MultiTenantMongoDatabaseFactory(String connectionString) {
        super(connectionString);
    }

    public MultiTenantMongoDatabaseFactory(ConnectionString connectionString) {
        super(connectionString);
    }

    public MultiTenantMongoDatabaseFactory(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    @Override
    public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
        var tenantId = TenantThreads.getTenantId();
        var newDbName = DEFAULT_DATABASE_NAME + "-" + tenantId;
        return super.getMongoDatabase(newDbName);
    }
}
