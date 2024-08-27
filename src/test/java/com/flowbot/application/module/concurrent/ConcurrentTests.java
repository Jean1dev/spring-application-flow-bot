package com.flowbot.application.module.concurrent;

import com.flowbot.application.SecurityTests;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.flowbot.application.E2ETests.MONGO_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes de concorrencia")
public class ConcurrentTests extends SecurityTests {

    public static final String DB_STARTS = "user";
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    @DisplayName("Deve separar os dados corretamente em chamadas concorrentes")
    void deveSepararDadosEmConcorrencia() throws Exception {
        MongoClient mongoClient = MongoClients.create(MONGO_CONTAINER.getReplicaSetUrl());
        mongoClient.listDatabaseNames().forEach(db -> {
            if (db.startsWith(DB_STARTS)) {
                mongoClient.getDatabase(db).drop();
            }
        });

        var usuario1 = super.createUserAndGetToken("usuario1", "XXXXXXXXXXXXXXXXXX");
        var usuario2 = super.createUserAndGetToken("usuario2", "XXXXXXXXXXXXXXXXXX");

        final var inputUsuario1 = new CriarNovoNumeroDto("nick do usuario1", "numero do usuario1");
        final var inputUsuario2 = new CriarNovoNumeroDto("nick do usuario2", "numero do usuario2");

        final var request1 = post("/numeros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputUsuario1))
                .with(request -> SecurityTests.setTokenOnHeader(request, usuario1));

        final var request2 = post("/numeros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputUsuario2))
                .with(request -> SecurityTests.setTokenOnHeader(request, usuario2));

        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        service.submit(() -> {
            try {
                this.mvc.perform(request1)
                        .andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });

        service.submit(() -> {
            try {
                this.mvc.perform(request2)
                        .andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        MongoIterable<String> databaseNames = mongoClient.listDatabaseNames();
        int expectetdDabasesFound = 0;
        for (String dbName : databaseNames) {
            if (dbName.startsWith(DB_STARTS)) {
                expectetdDabasesFound++;

                Document firsted = mongoClient.getDatabase(dbName).getCollection("numero").find().first();
                String nick = firsted.get("nick", String.class);
                if (
                        !nick.equals(inputUsuario1.nick()) &&
                                !nick.equals(inputUsuario2.nick())
                ) {
                    throw new RuntimeException("Dados não encontrados");
                }
            }
        }

        assertEquals(2, expectetdDabasesFound);
    }

    @Test
    @DisplayName("Um usuario nao pode conseguir acessar dados de outros")
    void naoDevePermitirAcessarDadosDeOutro() throws Exception {
        var usuario1 = super.getAcessToken();
        var usuario2 = super.createUserAndGetToken("usuario3-", "XXXXXXXXXXXXXXXXXX");

        final var inputUsuario1 = new CriarNovoNumeroDto("nick do usuario2", "numero do usuario2");

        final var request1 = post("/numeros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(inputUsuario1))
                .with(request -> SecurityTests.setTokenOnHeader(request, usuario1));

        final var idNumeroUsuario1 = this.mvc.perform(request1)
                .andDo(print())
                .andReturn()
                .getResponse()
                .getHeader("id");

        final var requestUsuario2 = get("/numeros/" + idNumeroUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(req -> SecurityTests.setTokenOnHeader(req, usuario2));

        final var responseUsuario2 = this.mvc.perform(requestUsuario2)
                .andDo(print());

        responseUsuario2.andExpect(status().is4xxClientError());

        final var requestUsuario1 = get("/numeros/" + idNumeroUsuario1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(req -> SecurityTests.setTokenOnHeader(req, usuario1));

        final var responseUsuario1 = this.mvc.perform(requestUsuario1)
                .andDo(print());

        responseUsuario1.andExpect(status().isOk());
    }
}
