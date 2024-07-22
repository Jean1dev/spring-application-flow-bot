package com.flowbot.application.module.domain.numeros.service;

import com.flowbot.application.E2ETests;
import com.flowbot.application.http.dtos.VerifyNumberResponse;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.flowbot.application.module.domain.numeros.NumerosFactory.umNumero;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VerificarTodosNumerosServiceTest extends E2ETests {

    @Autowired
    private NumeroMongoDbRepository repository;

    @Autowired
    private VerificarTodosNumerosService verificarTodosNumerosService;

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
    void verificar() throws InterruptedException {
        repository.saveAll(List.of(
                umNumero(StatusNumero.VALIDADO),
                umNumero(StatusNumero.VALIDADO),
                umNumero(StatusNumero.VALIDADO),
                umNumero(StatusNumero.BANIDO)
        ));

        when(botBuilderApi.verificarStatusDoNumero(anyString())).thenReturn(new VerifyNumberResponse(false));

        var executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        executor.submit(() -> verificarTodosNumerosService.verificar());

        executor.getThreadPoolExecutor().awaitTermination(2, TimeUnit.SECONDS);
        long count = repository.findAll()
                .stream()
                .filter(it -> it.getStatusNumero().equals(StatusNumero.PENDENTE))
                .count();

        assertEquals(3, count);

        verify(botBuilderApi, times(3)).verificarStatusDoNumero(anyString());
    }
}