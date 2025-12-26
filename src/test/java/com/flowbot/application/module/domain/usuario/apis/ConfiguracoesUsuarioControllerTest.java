package com.flowbot.application.module.domain.usuario.apis;

import com.flowbot.application.E2ETests;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import com.flowbot.application.module.domain.usuario.apis.dto.AtualizarConfiguracaoUsuarioDto;
import com.flowbot.application.module.domain.usuario.apis.dto.CriarConfiguracaoUsuarioDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioFactory.umaConfiguracao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ConfiguracoesUsuarioController Test")
class ConfiguracoesUsuarioControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));
    @Autowired
    private ConfiguracaoUsuarioRepository repository;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @BeforeEach
    void setUp() {
        TenantThreads.setTenantId("test-tenant-id");
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        TenantThreads.clear();
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar uma nova configuração com sucesso")
    void deveCriarNovaConfiguracao() throws Exception {
        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/logo.png", "Nome da Empresa");

        final var request = post("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.logoUrl").value("https://example.com/logo.png"))
                .andExpect(jsonPath("$.name").value("Nome da Empresa"))
                .andExpect(jsonPath("$.dataCriacao").exists());

        var configuracao = repository.findFirstBy().orElseThrow();
        assertEquals("https://example.com/logo.png", configuracao.getLogoUrl());
        assertEquals("Nome da Empresa", configuracao.getName());
    }

    @Test
    @DisplayName("Deve atualizar configuração existente ao criar novamente")
    void deveAtualizarConfiguracaoExistenteAoCriar() throws Exception {
        var configuracaoExistente = repository.save(umaConfiguracao("logo-antigo.png", "Nome Antigo"));

        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/novo-logo.png", "Novo Nome");

        final var request = post("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(configuracaoExistente.getId()))
                .andExpect(jsonPath("$.logoUrl").value("https://example.com/novo-logo.png"))
                .andExpect(jsonPath("$.name").value("Novo Nome"));

        var configuracaoAtualizada = repository.findByTenantId("test-tenant-id").orElseThrow();
        assertEquals("https://example.com/novo-logo.png", configuracaoAtualizada.getLogoUrl());
        assertEquals("Novo Nome", configuracaoAtualizada.getName());
    }

    @Test
    @DisplayName("Deve buscar configuração com sucesso")
    void deveBuscarConfiguracao() throws Exception {
        var configuracao = repository.save(umaConfiguracao("https://example.com/logo.png", "Nome da Empresa"));

        final var request = get("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(configuracao.getId()))
                .andExpect(jsonPath("$.logoUrl").value("https://example.com/logo.png"))
                .andExpect(jsonPath("$.name").value("Nome da Empresa"))
                .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 quando configuração não existe")
    void deveRetornar404QuandoConfiguracaoNaoExiste() throws Exception {
        final var request = get("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar configuração existente com PUT")
    void deveAtualizarConfiguracaoComPut() throws Exception {
        var configuracaoExistente = repository.save(umaConfiguracao("logo-antigo.png", "Nome Antigo"));

        var dto = new AtualizarConfiguracaoUsuarioDto("https://example.com/novo-logo.png", "Novo Nome");

        final var request = put("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isNoContent());

        var configuracaoAtualizada = repository.findByTenantId("test-tenant-id").orElseThrow();
        assertEquals("https://example.com/novo-logo.png", configuracaoAtualizada.getLogoUrl());
        assertEquals("Novo Nome", configuracaoAtualizada.getName());
    }

    @Test
    @DisplayName("Deve criar configuração quando não existe ao usar PUT")
    void deveCriarConfiguracaoQuandoNaoExisteComPut() throws Exception {
        var dto = new AtualizarConfiguracaoUsuarioDto("https://example.com/logo.png", "Nome da Empresa");

        final var request = put("/configuracoes-usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isNoContent());

        var configuracao = repository.findFirstBy().orElseThrow();
        assertEquals("https://example.com/logo.png", configuracao.getLogoUrl());
        assertEquals("Nome da Empresa", configuracao.getName());
    }

    @Test
    @DisplayName("Deve garantir que um usuário só tem uma configuração")
    void deveGarantirQueUsuarioSoTemUmaConfiguracao() throws Exception {
        var dto1 = new CriarConfiguracaoUsuarioDto("https://example.com/logo1.png", "Nome 1");
        var dto2 = new CriarConfiguracaoUsuarioDto("https://example.com/logo2.png", "Nome 2");

        this.mvc.perform(post("/configuracoes-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto1)))
                .andExpect(status().isOk());

        this.mvc.perform(post("/configuracoes-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto2)))
                .andExpect(status().isOk());

        var configuracoes = repository.findAll();
        assertEquals(1, configuracoes.size());
        assertEquals("https://example.com/logo2.png", configuracoes.get(0).getLogoUrl());
        assertEquals("Nome 2", configuracoes.get(0).getName());
    }

    @Test
    @DisplayName("Deve buscar configuração pública por tenant sem autenticação")
    void deveBuscarConfiguracaoPublicaPorTenant() throws Exception {
        var tenantId = "public-tenant-id";
        TenantThreads.setTenantId(tenantId);
        var configuracao = repository.save(umaConfiguracao("https://example.com/logo.png", "Nome Público"));

        final var request = get("/configuracoes-usuario/public/" + tenantId)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.logoUrl").value("https://example.com/logo.png"))
                .andExpect(jsonPath("$.name").value("Nome Público"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.dataCriacao").doesNotExist());
    }

    @Test
    @DisplayName("Deve retornar 400 quando tenant não existe no endpoint público")
    void deveRetornar400QuandoTenantNaoExiste() throws Exception {
        final var request = get("/configuracoes-usuario/public/tenant-inexistente")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isBadRequest());
    }
}

