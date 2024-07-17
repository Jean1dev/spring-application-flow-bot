package com.flowbot.application.module.domain.numeros.api;

import com.flowbot.application.E2ETests;
import com.flowbot.application.http.dtos.TypeBotAddInput;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.VincularTypeBotInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NumerosTypeBotControllerTest extends E2ETests {

    @Autowired
    private NumeroMongoDbRepository repository;

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
    void vincular() throws Exception {
        var id = repository.save(umNumero(StatusNumero.VALIDADO)).getId();
        var input = new VincularTypeBotInput(id, "typebot", "name");

        when(botBuilderApi.addTypeBot(any(TypeBotAddInput.class))).thenReturn(true);

        final var request = post("/api/v1/numeros-typebots/vincular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve buscar a quantidade de vinculos corretamente")
    void deveBUscarQuantidadeVinculos() throws Exception {
        final var request = get("/api/v1/numeros-typebots/quantidade-vinculos")
                .param("name", "name")
                .param("apiHost", "apiHost")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(0));
    }
}