package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GerenciamentoDoPlanoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private GerenciamentoDoPlanoUseCase useCase;
    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    void obterDadosPlano() {
        final var email = "john@doe.io";
        List<Plano> plansList = Collections.singletonList(PlanoFactory.umPlanoMensal(email));

        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(plansList);

        var dadosPlano = useCase.obterDadosPlano(email);

        assertNotNull(dadosPlano);
        assertEquals(email, dadosPlano.email());
        assertEquals(LocalDate.now().plusMonths(1), dadosPlano.vigenteAte());
    }
}