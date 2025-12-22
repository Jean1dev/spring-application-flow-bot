package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.context.MultiTenantMongoDatabaseFactory;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AcessoOutputDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.RegistarAcessoDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
public class GerenciamentoDoPlanoUseCase {
    private final MongoTemplate mongoTemplate;
    private final String connectionString;

    public GerenciamentoDoPlanoUseCase(MongoTemplate mongoTemplate, @Value("${spring.data.mongodb.uri}") String connectionString) {
        this.mongoTemplate = mongoTemplate;
        this.connectionString = connectionString;
    }

    public List<PlanoAtivoOutput> listPlanosAtivos() {
        return mongoTemplate.findAll(Plano.class)
                .stream()
                .map(PlanoAtivoOutput::map)
                .toList();
    }

    public PlanoAtivoOutput obterDadosPlano(String email) {
        var tenantId = TenantThreads.getTenantId();
        if (tenantId == null || tenantId.isEmpty()) {
            return buscarPlanoEmTodosTenants(email);
        }
        return mongoTemplate.findAll(Plano.class)
                .stream()
                .filter(plano -> plano.getUsuario().email().equals(email))
                .findFirst()
                .map(PlanoAtivoOutput::map)
                .orElseThrow();
    }

    private PlanoAtivoOutput buscarPlanoEmTodosTenants(String email) {
        var query = new Query().addCriteria(Criteria.where("usuario.email").is(email));

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            var databaseNames = mongoClient.listDatabaseNames();

            return StreamSupport.stream(databaseNames.spliterator(), false)
                    .filter(dbName -> dbName.startsWith(MultiTenantMongoDatabaseFactory.DEFAULT_DATABASE_NAME + "-"))
                    .map(dbName -> {
                        var dbFactory = new SimpleMongoClientDatabaseFactory(mongoClient, dbName);
                        var template = new MongoTemplate(dbFactory);
                        return template.find(query, Plano.class)
                                .stream()
                                .findFirst()
                                .map(PlanoAtivoOutput::map)
                                .orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
        }
    }

    public AcessoOutputDto registarAcesso(RegistarAcessoDto registarAcessoDto) {
        var plano = getPlanoById(registarAcessoDto.email());
        var isFirstAccess = isFirstAccess(plano.getId());
        var acesso = new Acesso(registarAcessoDto.fonte(), registarAcessoDto.localizacao(), plano.getId());

        mongoTemplate.save(acesso);
        return new AcessoOutputDto(isFirstAccess, PlanoAtivoOutput.map(plano));
    }

    private Plano getPlanoById(String email) {
        var query = new Query().addCriteria(Criteria.where("usuario.email").is(email));
        return mongoTemplate.find(query, Plano.class)
                .stream()
                .findFirst()
                .orElseThrow();
    }

    private boolean isFirstAccess(String planoRef) {
        return mongoTemplate.findAll(Acesso.class)
                .stream()
                .noneMatch(acesso -> acesso.getPlanoRef().equals(planoRef));
    }

    public void processarReembolso(String email) {
        var plano = getPlanoById(email);
        plano.processarReembolso();
        mongoTemplate.save(plano);
    }
}
