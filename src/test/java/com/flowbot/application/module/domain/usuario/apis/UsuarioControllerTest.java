package com.flowbot.application.module.domain.usuario.apis;

import com.flowbot.application.E2ETests;
import com.flowbot.application.context.TenantThreads;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UsuarioController Test")
class UsuarioControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        TenantThreads.setTenantId("test-tenant-id");
    }

    @AfterEach
    void tearDown() {
        TenantThreads.clear();
    }

    @Test
    @DisplayName("Deve retornar o tenant do usuário autenticado")
    void deveRetornarTenantDoUsuario() throws Exception {
        final var request = get("/usuario/tenant")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value("test-tenant-id"));
    }

    @Test
    @DisplayName("Deve retornar string vazia quando não há tenant definido")
    void deveRetornarStringVaziaQuandoNaoHaTenant() throws Exception {
        TenantThreads.clear();

        final var request = get("/usuario/tenant")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(""));
    }
}

