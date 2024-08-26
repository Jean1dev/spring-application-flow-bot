package com.flowbot.application.module.domain.financeiro.assinaturas.api;

import com.flowbot.application.E2ETests;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.CriarPlanoInputDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlanoControllerTest extends E2ETests {

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

    @Test
    void criarPlano() throws Exception {
        var inputDto = new CriarPlanoInputDto("XXXXXXXXXXXXXXX", "MENSAL");
        final var request = post("/plano")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputDto));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
        assertNotNull(response.andReturn().getResponse().getHeader("id"));
    }
}