package com.flowbot.application.module.security;

import com.flowbot.application.SecurityTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static com.flowbot.application.E2ETests.MONGO_VERSION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes de seguranca da aplicacao")
public class AuthTests extends SecurityTests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    static void setUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @Test
    @DisplayName("Nao deve permitir acessar os recursos pq nao esta autorizado")
    void deveReceber401() throws Exception {
        final var request = get("/numeros");

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("nao permitir permitir acesso com token invalido")
    void naoPermitirAcessoComTokenInvalido() throws Exception {
        final var token = "xpto";
        final var request = get("/numeros")
                .with(req -> SecurityTests.setTokenOnHeader(req, token));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve Permitir acesso em recursos autorizados")
    void devePermitirAcessoEmRecursosLiberados() throws Exception {
        final var request = get("/plano/vigente?email=xxxx");

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("deve permitir requisicao pois esta autorizado")
    void deveReceber200() throws Exception {
        final var token = super.getAcessToken();
        final var request = get("/numeros")
                .param("sortByNewest", "true")
                .param("terms", "teste")
                .with(req -> SecurityTests.setTokenOnHeader(req, token));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }
}
