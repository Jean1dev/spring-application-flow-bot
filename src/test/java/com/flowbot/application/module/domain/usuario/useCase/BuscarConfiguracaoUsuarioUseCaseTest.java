package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioFactory.umaConfiguracao;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("BuscarConfiguracaoUsuarioUseCase Test")
class BuscarConfiguracaoUsuarioUseCaseTest extends UseCaseTest {

    @InjectMocks
    private BuscarConfiguracaoUsuarioUseCase useCase;

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
    @DisplayName("Deve buscar configuração com sucesso")
    void deveBuscarConfiguracaoComSucesso() {
        var configuracao = umaConfiguracao();
        configuracao = new ConfiguracaoUsuario("id-123", "test-tenant-id", "https://example.com/logo.png", "Nome da Empresa", null);

        when(repository.findByTenantId("test-tenant-id")).thenReturn(Optional.of(configuracao));

        var result = useCase.execute();

        assertNotNull(result);
        assertEquals("id-123", result.getId());
        assertEquals("test-tenant-id", result.getTenantId());
        assertEquals("https://example.com/logo.png", result.getLogoUrl());
        assertEquals("Nome da Empresa", result.getName());
        verify(repository, times(1)).findByTenantId("test-tenant-id");
    }

    @Test
    @DisplayName("Deve lançar exceção quando configuração não é encontrada")
    void deveLancarExcecaoQuandoConfiguracaoNaoEncontrada() {
        when(repository.findByTenantId("test-tenant-id")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> useCase.execute());
        verify(repository, times(1)).findByTenantId("test-tenant-id");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é nulo")
    void deveLancarExcecaoQuandoTenantIdNulo() {
        TenantThreads.clear();

        assertThrows(ValidationException.class, () -> useCase.execute());
        verify(repository, never()).findByTenantId(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é vazio")
    void deveLancarExcecaoQuandoTenantIdVazio() {
        TenantThreads.setTenantId("");

        assertThrows(ValidationException.class, () -> useCase.execute());
        verify(repository, never()).findByTenantId(anyString());
    }
}

