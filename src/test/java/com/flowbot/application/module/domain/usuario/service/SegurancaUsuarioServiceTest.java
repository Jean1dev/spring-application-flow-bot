package com.flowbot.application.module.domain.usuario.service;

import com.flowbot.application.E2ETests;
import com.flowbot.application.module.domain.usuario.ChavesPublicasDoUsuario;
import com.flowbot.application.module.domain.usuario.ChavesUsuarioTypeBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

class SegurancaUsuarioServiceTest extends E2ETests {

    private SegurancaUsuarioService service;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        service = new SegurancaUsuarioService(mongoTemplate, "chave-privada-test");
    }

    @Test
    void testeGeraldeCryptografia() {
        // getKeys()
        ChavesPublicasDoUsuario keys = service.getKeys();
        assertNotNull(keys);
        assertNotNull(keys.publicKey());
        assertNotNull(keys.publicKey());

        // saveApiKeys()
        final var cryptoTOkenExpected = "token";
        var chavesUsuarioTypeBot = new ChavesUsuarioTypeBot(cryptoTOkenExpected, "workspace");

        service.saveApiKeys(chavesUsuarioTypeBot);

        // getApisKeys()

        ChavesUsuarioTypeBot apisKeys = service.getApisKeys();
        assertNotNull(apisKeys);
        assertEquals(cryptoTOkenExpected, apisKeys.typebot_token());
        assertEquals("workspace", apisKeys.typebot_workspaceId());
    }

}