package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.UsuarioDoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.RegistarAcessoDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GerenciamentoDoPlanoUseCaseTest extends UseCaseTest {

    private GerenciamentoDoPlanoUseCase useCase;
    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        TenantThreads.setTenantId("test-tenant");
        useCase = new GerenciamentoDoPlanoUseCase(mongoTemplate, "");
    }

    @AfterEach
    void tearDown() {
        TenantThreads.clear();
    }

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

    @Test
    @DisplayName("Usuario ja tem um plano entao deve retornar que nao eh o primeiro acesso")
    void naoEHPrimeiroAcesso() {
        final var email = "john@doe.io";
        var plano = mock(Plano.class);
        when(plano.getId()).thenReturn("planoId");
        when(plano.getFinalizaEm()).thenReturn(LocalDateTime.now().plusYears(1));

        final var usuarioMock = mock(UsuarioDoPlano.class);
        when(usuarioMock.email()).thenReturn(email);

        when(plano.getUsuario()).thenReturn(usuarioMock);

        List<Plano> plansList = Collections.singletonList(plano);

        when(mongoTemplate.find(any(Query.class), eq(Plano.class)))
                .thenReturn(plansList);

        var acessosList = Collections.singletonList(new Acesso("source", "location", plansList.get(0).getId()));

        when(mongoTemplate.findAll(eq(Acesso.class)))
                .thenReturn(acessosList);

        var registarAcessoDto = new RegistarAcessoDto("source", "location", email);

        var acessoOutputDto = useCase.registarAcesso(registarAcessoDto);
        assertNotNull(acessoOutputDto);
        assertFalse(acessoOutputDto.firstAccess());
    }

    @Test
    @DisplayName("Deve processar reembolso e atualizar vencimento do plano para 5 dias")
    void processarReembolso() {
        final var email = "john@doe.io";
        var plano = mock(Plano.class);

        final var usuarioMock = mock(UsuarioDoPlano.class);

        List<Plano> plansList = Collections.singletonList(plano);

        when(mongoTemplate.find(any(Query.class), eq(Plano.class)))
                .thenReturn(plansList);

        useCase.processarReembolso(email);

        verify(plano).processarReembolso();
        verify(mongoTemplate).save(plano);
    }
}