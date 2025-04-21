package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.UsuarioDoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.RegistarAcessoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
}