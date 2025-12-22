package com.flowbot.application.module.domain.financeiro.assinaturas.api;

import com.flowbot.application.E2ETests;
import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.AcessoFactory;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnomaliaAcessoControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @Autowired
    private MongoTemplate mongoTemplate;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Acesso.class);
        mongoTemplate.dropCollection(Plano.class);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há anomalias")
    void retornarListaVaziaQuandoNaoHaAnomalias() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosNormais = AcessoFactory.acessosNormais(plano.getId());
        acessosNormais.forEach(mongoTemplate::save);

        var request = get("/anomalias-acesso")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deve detectar e retornar anomalias quando há acessos suspeitos")
    void detectarERetornarAnomalias() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(plano.getId());
        acessosSuspeitos.forEach(mongoTemplate::save);

        var request = get("/anomalias-acesso")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].planoId").value(plano.getId()))
                .andExpect(jsonPath("$[0].emailUsuario").value(email))
                .andExpect(jsonPath("$[0].totalAcessos").value(acessosSuspeitos.size()))
                .andExpect(jsonPath("$[0].nivelSuspeita").exists())
                .andExpect(jsonPath("$[0].motivosDeteccao").isArray())
                .andExpect(jsonPath("$[0].detalhesAcessos").isArray());
    }

    @Test
    @DisplayName("Deve filtrar apenas anomalias críticas")
    void filtrarApenasAnomaliasCriticas() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(plano.getId());
        acessosSuspeitos.forEach(mongoTemplate::save);

        var request = get("/anomalias-acesso/criticas")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve filtrar anomalias suspeitas (alto risco e críticas)")
    void filtrarAnomaliasSuspeitas() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(plano.getId());
        acessosSuspeitos.forEach(mongoTemplate::save);

        var request = get("/anomalias-acesso/suspeitas")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve filtrar anomalias por período específico")
    void filtrarAnomaliasPorPeriodo() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosSuspeitos = AcessoFactory.multiplosAcessosSuspeitos(plano.getId());
        acessosSuspeitos.forEach(mongoTemplate::save);

        var inicio = YearMonth.now().minusMonths(1);
        var fim = YearMonth.now();

        var request = get("/anomalias-acesso/periodo")
                .param("inicio", inicio.toString())
                .param("fim", fim.toString())
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Deve retornar erro 400 para parâmetros de data inválidos")
    void retornarErroParaParametrosDataInvalidos() throws Exception {
        var request = get("/anomalias-acesso/periodo")
                .param("inicio", "2025-13")
                .param("fim", "ano-mes-invalido")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve detectar anomalia com múltiplas origens")
    void detectarAnomaliaComMultiplasOrigens() throws Exception {
        final var email = "john@doe.io";
        var plano = PlanoFactory.umPlanoMensal(email);
        mongoTemplate.save(plano);

        var acessosMultiplasOrigens = AcessoFactory.acessosComMultiplasOrigens(plano.getId());
        acessosMultiplasOrigens.forEach(mongoTemplate::save);

        var request = get("/anomalias-acesso")
                .contentType(MediaType.APPLICATION_JSON);

        var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].origensUnicas").value(4))
                .andExpect(jsonPath("$[0].motivosDeteccao").isArray());
    }
}
