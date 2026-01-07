package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.context.MultiTenantMongoDatabaseFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.PlanoMultiTenantOutputDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class BuscarAssinaturaTodosTenantsUseCase {
    private static final Logger log = LoggerFactory.getLogger(BuscarAssinaturaTodosTenantsUseCase.class);
    private static final String COLLECTION_NAME = "plano";
    private final MongoTemplate mongoTemplate;
    private final String connectionString;

    public BuscarAssinaturaTodosTenantsUseCase(MongoTemplate mongoTemplate,
                                               @Value("${spring.data.mongodb.uri}") String connectionString) {
        this.mongoTemplate = mongoTemplate;
        this.connectionString = connectionString;
    }

    public List<PlanoMultiTenantOutputDto> buscarAssinaturaPorEmail(String email) {
        List<PlanoMultiTenantOutputDto> resultados = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            List<String> databaseNames = StreamSupport.stream(
                    mongoClient.listDatabaseNames().spliterator(), false
            ).toList();

            for (String dbName : databaseNames) {
                if (dbName.startsWith(MultiTenantMongoDatabaseFactory.DEFAULT_DATABASE_NAME + "-")) {
                    try {
                        MongoDatabase database = mongoClient.getDatabase(dbName);
                        boolean collectionExists = StreamSupport.stream(
                                database.listCollectionNames().spliterator(), false
                        ).anyMatch(name -> name.equals(COLLECTION_NAME));

                        if (collectionExists) {
                            String tenantId = dbName.substring(MultiTenantMongoDatabaseFactory.DEFAULT_DATABASE_NAME.length() + 1);
                            MongoTemplate tenantMongoTemplate = new MongoTemplate(
                                    new SimpleMongoClientDatabaseFactory(mongoClient, dbName)
                            );

                            Query query = new Query().addCriteria(Criteria.where("usuario.email").is(email));
                            List<Plano> planos = tenantMongoTemplate.find(query, Plano.class);

                            for (Plano plano : planos) {
                                resultados.add(new PlanoMultiTenantOutputDto(
                                        tenantId,
                                        PlanoAtivoOutput.map(plano)
                                ));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Erro ao buscar assinatura no banco {}: {}", dbName, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro ao listar bancos de dados: {}", e.getMessage(), e);
        }

        return resultados;
    }
}
