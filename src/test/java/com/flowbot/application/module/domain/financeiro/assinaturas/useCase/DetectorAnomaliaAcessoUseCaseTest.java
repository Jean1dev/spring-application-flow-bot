package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.AcessoFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.UsuarioDoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AnomaliaAcessoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DetectorAnomaliaAcessoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DetectorAnomaliaAcessoUseCase useCase;

    @Mock
    private MongoTemplate mongoTemplate;

    private Plano criarPlanoMock(String planoId, String email) {
        var plano = mock(Plano.class);
        var usuario = mock(UsuarioDoPlano.class);

        lenient().when(usuario.email()).thenReturn(email);
        lenient().when(plano.getId()).thenReturn(planoId);
        lenient().when(plano.getUsuario()).thenReturn(usuario);

        return plano;
    }

    @Test
    @DisplayName("Deve detectar anomalia quando há múltiplos acessos suspeitos")
    void detectarAnomaliaComMultiplosAcessos() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-123";
        var plano = criarPlanoMock(planoId, email);
        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(planoId);

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosSuspeitos);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomalias();

        assertFalse(anomalias.isEmpty());
        var anomalia = anomalias.getFirst();
        assertEquals(planoId, anomalia.planoId());
        assertEquals(email, anomalia.emailUsuario());
        assertTrue(anomalia.totalAcessos() >= 5);
        assertNotNull(anomalia.nivelSuspeita());
        assertFalse(anomalia.motivosDeteccao().isEmpty());
    }

    @Test
    @DisplayName("Não deve detectar anomalia com acessos normais")
    void naoDetectarAnomaliaComAcessosNormais() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-456";
        var plano = criarPlanoMock(planoId, email);
        var acessosNormais = AcessoFactory.acessosNormais(planoId);

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosNormais);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomalias();

        assertTrue(anomalias.isEmpty());
    }

    @Test
    @DisplayName("Deve detectar anomalia com múltiplas origens de acesso")
    void detectarAnomaliaComMultiplasOrigens() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-789";
        var plano = criarPlanoMock(planoId, email);
        var acessosMultiplasOrigens = AcessoFactory.acessosComMultiplasOrigens(planoId);

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosMultiplasOrigens);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomalias();

        assertFalse(anomalias.isEmpty());
        var anomalia = anomalias.getFirst();
        assertTrue(anomalia.origensUnicas() >= 3);
        assertTrue(anomalia.motivosDeteccao().stream()
                .anyMatch(motivo -> motivo.contains("origens")));
    }

    @Test
    @DisplayName("Deve detectar anomalias por período específico")
    void detectarAnomaliasPorPeriodo() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-period";
        var plano = criarPlanoMock(planoId, email);
        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(planoId);

        var inicio = LocalDateTime.now().minusDays(30);
        var fim = LocalDateTime.now();

        when(mongoTemplate.find(any(Query.class), eq(Acesso.class))).thenReturn(acessosSuspeitos);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomaliasPorPeriodo(inicio, fim);

        assertFalse(anomalias.isEmpty());
        var anomalia = anomalias.getFirst();
        assertEquals(planoId, anomalia.planoId());
        assertEquals(email, anomalia.emailUsuario());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há acessos")
    void retornarListaVaziaQuandoNaoHaAcessos() {
        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(Collections.emptyList());
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(Collections.emptyList());

        var anomalias = useCase.detectarAnomalias();

        assertTrue(anomalias.isEmpty());
    }

    @Test
    @DisplayName("Deve ignorar planos que não existem mais")
    void ignorarPlanosInexistentes() {
        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos("plano-inexistente");

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosSuspeitos);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(Collections.emptyList());

        var anomalias = useCase.detectarAnomalias();

        assertTrue(anomalias.isEmpty());
    }

    @Test
    @DisplayName("Deve classificar nível de suspeita corretamente")
    void classificarNivelSuspeitaCorretamente() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-nivel";
        var plano = criarPlanoMock(planoId, email);
        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(planoId);

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosSuspeitos);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomalias();

        assertFalse(anomalias.isEmpty());
        var anomalia = anomalias.getFirst();
        assertNotNull(anomalia.nivelSuspeita());
        assertTrue(anomalia.nivelSuspeita().ordinal() >= AnomaliaAcessoDto.NivelSuspeita.MEDIO.ordinal());
    }

    @Test
    @DisplayName("Deve incluir detalhes dos acessos na anomalia")
    void incluirDetalhesAcessosNaAnomalia() {
        final var email = "john@doe.io";
        final var planoId = "plano-id-detalhes";
        var plano = criarPlanoMock(planoId, email);
        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(planoId);

        when(mongoTemplate.findAll(eq(Acesso.class))).thenReturn(acessosSuspeitos);
        when(mongoTemplate.findAll(eq(Plano.class))).thenReturn(List.of(plano));

        var anomalias = useCase.detectarAnomalias();

        assertFalse(anomalias.isEmpty());
        var anomalia = anomalias.getFirst();
        assertFalse(anomalia.detalhesAcessos().isEmpty());
        assertEquals(acessosSuspeitos.size(), anomalia.detalhesAcessos().size());

        var detalhe = anomalia.detalhesAcessos().getFirst();
        assertNotNull(detalhe.dataAcesso());
        assertNotNull(detalhe.origem());
        assertNotNull(detalhe.localizacao());
    }
}
