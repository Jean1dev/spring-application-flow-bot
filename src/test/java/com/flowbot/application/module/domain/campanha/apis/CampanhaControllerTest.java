package com.flowbot.application.module.domain.campanha.apis;

import com.flowbot.application.E2ETests;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
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

import java.util.List;

import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanha;
import static com.flowbot.application.module.domain.campanha.CampanhaFactory.umaCampanhaComArquivos;
import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampanhaControllerTest extends E2ETests {

    @Autowired
    private NumeroMongoDbRepository numeroMongoDbRepository;
    @Autowired
    private CampanhaMongoDBRepository campanhaMongoDBRepository;

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
    @DisplayName("Deve buscar todos os arquivos da campanha")
    void deveBUscarArquivos() throws Exception {
        var arquivos = List.of("arquivo1", "arquivo2");
        var id = campanhaMongoDBRepository.save(umaCampanhaComArquivos(arquivos)).getId();

        final var request = get("/campanhas/" + id + "/arquivos");
        final var response = this.mvc.perform(request).andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(is(arquivos.get(0))))
                .andExpect(jsonPath("$[1]").value(is(arquivos.get(1))));
    }

    @Test
    @DisplayName("Teste de delecao de campanha")
    void deleteCampanha() throws Exception {
        var id = campanhaMongoDBRepository.save(umaCampanha()).getId();

        final var request = delete("/campanhas/" + id);
        final var response = this.mvc.perform(request).andDo(print());

        response.andExpect(status().isNoContent());

        assertTrue(campanhaMongoDBRepository.findById(id).isEmpty());
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

    @Test
    @DisplayName("Teste de busca todos")
    void buscarTodos() throws Exception {
        campanhaMongoDBRepository.deleteAll();
        campanhaMongoDBRepository.saveAll(List.of(
                umaCampanha(),
                umaCampanha(numeroMongoDbRepository.save(umNumero(StatusNumero.VALIDADO)).getId())
        ));

        final var request = get("/campanhas")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(is(2)))
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].numero").value(is("sem numero")))
                .andExpect(jsonPath("$.content[1].numero").value(is("numero")));
    }
}