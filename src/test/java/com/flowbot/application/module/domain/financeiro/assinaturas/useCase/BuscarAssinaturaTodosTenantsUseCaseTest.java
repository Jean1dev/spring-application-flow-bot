package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuscarAssinaturaTodosTenantsUseCaseTest extends UseCaseTest {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017/test";
    private BuscarAssinaturaTodosTenantsUseCase useCase;
    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        useCase = new BuscarAssinaturaTodosTenantsUseCase(mongoTemplate, CONNECTION_STRING);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há bancos de dados ou não encontra assinatura")
    void deveRetornarListaVaziaQuandoNaoEncontra() {
        var email = "test@example.com";
        var result = useCase.buscarAssinaturaPorEmail(email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista quando email é nulo ou vazio")
    void deveRetornarListaVaziaQuandoEmailInvalido() {
        var result1 = useCase.buscarAssinaturaPorEmail(null);
        var result2 = useCase.buscarAssinaturaPorEmail("");

        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }
}
