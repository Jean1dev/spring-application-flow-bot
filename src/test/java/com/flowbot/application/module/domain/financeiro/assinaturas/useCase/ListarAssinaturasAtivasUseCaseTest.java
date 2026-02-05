package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.UsuarioDoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AssinaturaAtivaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListarAssinaturasAtivasUseCaseTest extends UseCaseTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private ListarAssinaturasAtivasUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarAssinaturasAtivasUseCase(mongoTemplate);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há planos ativos")
    void deveRetornarListaVaziaQuandoNaoHaPlanosAtivos() {
        when(mongoTemplate.find(any(Query.class), eq(Plano.class))).thenReturn(Collections.emptyList());

        List<AssinaturaAtivaDto> result = useCase.listar();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar assinaturas ativas com usuario e ativoHa")
    void deveRetornarAssinaturasAtivasComUsuarioEAtivoHa() {
        Plano plano = PlanoFactory.umPlanoMensal("joao@email.com");
        when(mongoTemplate.find(any(Query.class), eq(Plano.class))).thenReturn(List.of(plano));

        List<AssinaturaAtivaDto> result = useCase.listar();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("joao@email.com", result.get(0).usuario());
        assertNotNull(result.get(0).ativoHa());
        assertTrue(result.get(0).ativoHa().contains("dia") || result.get(0).ativoHa().contains("dias"));
    }

    @Test
    @DisplayName("Deve usar nick quando preenchido, senao email")
    void deveUsarNickQuandoPreenchido() {
        Plano plano = mock(Plano.class);
        when(plano.getUsuario()).thenReturn(new UsuarioDoPlano("maria", "maria@email.com", null));
        when(plano.getDataCriacao()).thenReturn(LocalDateTime.now().minusDays(1));
        when(mongoTemplate.find(any(Query.class), eq(Plano.class))).thenReturn(List.of(plano));

        List<AssinaturaAtivaDto> result = useCase.listar();

        assertEquals(1, result.size());
        assertEquals("maria", result.get(0).usuario());
    }

    @Test
    @DisplayName("Deve ordenar por maior tempo de assinatura primeiro")
    void deveOrdenarPorMaiorTempoPrimeiro() {
        Plano planoRecente = mock(Plano.class);
        when(planoRecente.getUsuario()).thenReturn(new UsuarioDoPlano("recente", "recente@email.com", null));
        when(planoRecente.getDataCriacao()).thenReturn(LocalDateTime.now().minusDays(5));

        Plano planoAntigo = mock(Plano.class);
        when(planoAntigo.getUsuario()).thenReturn(new UsuarioDoPlano("antigo", "antigo@email.com", null));
        when(planoAntigo.getDataCriacao()).thenReturn(LocalDateTime.now().minusDays(90));

        when(mongoTemplate.find(any(Query.class), eq(Plano.class)))
                .thenReturn(List.of(planoRecente, planoAntigo));

        List<AssinaturaAtivaDto> result = useCase.listar();

        assertEquals(2, result.size());
        assertEquals("antigo", result.get(0).usuario());
        assertEquals("recente", result.get(1).usuario());
    }
}
