package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import com.flowbot.application.module.domain.usuario.apis.dto.AtualizarConfiguracaoUsuarioDto;
import com.flowbot.application.module.domain.usuario.apis.dto.CriarConfiguracaoUsuarioDto;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SalvarConfiguracaoUsuarioUseCase Test")
class SalvarConfiguracaoUsuarioUseCaseTest extends UseCaseTest {

    @InjectMocks
    private SalvarConfiguracaoUsuarioUseCase useCase;

    @Mock
    private ConfiguracaoUsuarioRepository repository;

    @BeforeEach
    void setUp() {
        TenantThreads.setTenantId("test-tenant-id");
    }

    @AfterEach
    void tearDown() {
        TenantThreads.clear();
    }

    @Test
    @DisplayName("Deve criar uma nova configuração quando não existe")
    void deveCriarNovaConfiguracao() {
        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/logo.png", "Nome da Empresa");
        var configuracaoSalva = new ConfiguracaoUsuario("id-123", dto.logoUrl(), dto.name(), null);

        when(repository.findFirstBy()).thenReturn(Optional.empty());
        when(repository.save(any(ConfiguracaoUsuario.class))).thenReturn(configuracaoSalva);

        var result = useCase.execute(dto);

        assertNotNull(result);
        assertEquals("https://example.com/logo.png", result.getLogoUrl());
        assertEquals("Nome da Empresa", result.getName());
        verify(repository, times(1)).save(any(ConfiguracaoUsuario.class));
    }

    @Test
    @DisplayName("Deve atualizar configuração existente quando já existe")
    void deveAtualizarConfiguracaoExistente() {
        var configuracaoExistente = new ConfiguracaoUsuario("id-123", "logo-antigo.png", "Nome Antigo", null);

        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/novo-logo.png", "Novo Nome");
        var configuracaoAtualizada = new ConfiguracaoUsuario("id-123", dto.logoUrl(), dto.name(), null);

        when(repository.findFirstBy()).thenReturn(Optional.of(configuracaoExistente));
        when(repository.save(any(ConfiguracaoUsuario.class))).thenReturn(configuracaoAtualizada);

        var result = useCase.execute(dto);

        assertNotNull(result);
        assertEquals("https://example.com/novo-logo.png", result.getLogoUrl());
        assertEquals("Novo Nome", result.getName());
        verify(repository, times(1)).save(configuracaoExistente);
    }

    @Test
    @DisplayName("Deve atualizar usando AtualizarConfiguracaoUsuarioDto")
    void deveAtualizarComAtualizarDto() {
        var configuracaoExistente = new ConfiguracaoUsuario("id-123", "logo-antigo.png", "Nome Antigo", null);

        var dto = new AtualizarConfiguracaoUsuarioDto("https://example.com/novo-logo.png", "Novo Nome");
        var configuracaoAtualizada = new ConfiguracaoUsuario("id-123", dto.logoUrl(), dto.name(), null);

        when(repository.findFirstBy()).thenReturn(Optional.of(configuracaoExistente));
        when(repository.save(any(ConfiguracaoUsuario.class))).thenReturn(configuracaoAtualizada);

        var result = useCase.execute(dto);

        assertNotNull(result);
        assertEquals("https://example.com/novo-logo.png", result.getLogoUrl());
        assertEquals("Novo Nome", result.getName());
        verify(repository, times(1)).save(configuracaoExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é nulo")
    void deveLancarExcecaoQuandoTenantIdNulo() {
        TenantThreads.clear();

        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/logo.png", "Nome da Empresa");

        assertThrows(ValidationException.class, () -> useCase.execute(dto));
        verify(repository, never()).save(any(ConfiguracaoUsuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é vazio")
    void deveLancarExcecaoQuandoTenantIdVazio() {
        TenantThreads.setTenantId("");

        var dto = new CriarConfiguracaoUsuarioDto("https://example.com/logo.png", "Nome da Empresa");

        assertThrows(ValidationException.class, () -> useCase.execute(dto));
        verify(repository, never()).save(any(ConfiguracaoUsuario.class));
    }
}

