package com.flowbot.application.module.domain.playground.api;

import com.flowbot.application.E2ETests;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayGroundControllerTest extends E2ETests {

    @Autowired
    private NumeroMongoDbRepository numeroMongoDbRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @Test
    void execute() throws Exception {
        var id = numeroMongoDbRepository.save(umNumero(StatusNumero.VALIDADO)).getId();
        var map = Map.of(
                "senderId", id,
                "recipientNumber", "2",
                "message", "ola");

        when(botBuilderApi.playground(anyMap())).thenReturn(true);

        final var request = post("/playground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(map));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.needValidadeNumber").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.senderId").exists())
                .andExpect(jsonPath("$.senderId").value(id))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.needValidadeNumber").value(false))
                .andExpect(jsonPath("$.message").value("Playground enviado com sucesso"));
    }
}