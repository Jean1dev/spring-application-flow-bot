package com.flowbot.application.module.domain.numeros.service;

import com.flowbot.application.E2ETests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RelacionamentoNumeroTypeBotServiceTest")
class RelacionamentoNumeroTypeBotServiceTest extends E2ETests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RelacionamentoNumeroTypeBotService service;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @AfterEach
    public void cleanUp() {
        mongoTemplate.dropCollection(RelacionamentoNumeroTypeBotService.COLLECTION_NAME);
    }

    @Test
    @DisplayName("Deve relacionar um registro com sucesso")
    void relacionar() {
        final var apiHost = "host";
        final var name = "typebo";
        final var numeroId = "numeroId";

        service.relacionar(numeroId, name, apiHost);
        final var relacionamento = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where("numeroId").is(numeroId)),
                RelacionamentoNumeroTypeBotService.RelacionamentoNumeroTypeBot.class,
                RelacionamentoNumeroTypeBotService.COLLECTION_NAME);

        assertEquals(numeroId, relacionamento.numeroId());
        assertEquals(name, relacionamento.name());
        assertEquals(apiHost, relacionamento.apiHost());
    }

    @Test
    @DisplayName("Deve limpar registros que tem que ser sobrescritos")
    void deveLimparRegistrosQueTemQueSerSobrescritos() {
        final var apiHost = "host";
        final var name = "typebo";
        final var numeroId = "numeroId";

        service.relacionar(numeroId, name, apiHost);
        service.relacionar(numeroId, "name1", apiHost);
        service.relacionar(numeroId, name, apiHost);

        long count = mongoTemplate.count(new Query(), RelacionamentoNumeroTypeBotService.COLLECTION_NAME);
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve buscar a quantidade de vinculos corretamente")
    void deveBuscarAQuantidadeDeVinculosCorretamente() {
        final var apiHost = "host";
        final var name = "typeboname";
        final var numeroId = "numeroId";

        mongoTemplate.save(new RelacionamentoNumeroTypeBotService.RelacionamentoNumeroTypeBot(
                numeroId,
                name,
                apiHost
        ), RelacionamentoNumeroTypeBotService.COLLECTION_NAME);

        mongoTemplate.save(new RelacionamentoNumeroTypeBotService.RelacionamentoNumeroTypeBot(
                numeroId,
                name,
                apiHost
        ), RelacionamentoNumeroTypeBotService.COLLECTION_NAME);

        mongoTemplate.save(new RelacionamentoNumeroTypeBotService.RelacionamentoNumeroTypeBot(
                numeroId,
                "name",
                apiHost
        ), RelacionamentoNumeroTypeBotService.COLLECTION_NAME);

        long count = service.quantidadeDeNumerosVinculadosNoTypebot(name, apiHost);
        assertEquals(2, count);
    }
}