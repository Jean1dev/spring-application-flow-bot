package com.flowbot.application.module.domain.campanha.apis;

import com.flowbot.application.E2ETests;
import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanha;
import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampanhaControllerTest extends E2ETests {

    @Autowired
    private NumeroMongoDbRepository numeroMongoDbRepository;
    @Autowired
    private CampanhaMongoDBRepository campanhaMongoDBRepository;
    @MockBean
    private BotBuilderApi botBuilderApi;

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
    @DisplayName("Deve disparar uma campanha corretamente")
    void dispararCampanha() throws Exception {
        var idNumero = numeroMongoDbRepository.save(umNumero(StatusNumero.VALIDADO)).getId();
        var idCampanha = campanhaMongoDBRepository.save(umaCampanha(idNumero)).getId();

        var batchOutput = new BatchSendResponse("execID");
        when(botBuilderApi.batchSend(anyMap())).thenReturn(batchOutput);

        final var request = post("/campanhas/disparar/" + idCampanha)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionID").value("execID"));
    }
}