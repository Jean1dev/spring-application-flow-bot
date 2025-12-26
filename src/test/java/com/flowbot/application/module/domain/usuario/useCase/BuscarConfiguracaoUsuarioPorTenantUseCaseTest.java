package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.UseCaseTest;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("BuscarConfiguracaoUsuarioPorTenantUseCase Test")
class BuscarConfiguracaoUsuarioPorTenantUseCaseTest extends UseCaseTest {

    @InjectMocks
    private BuscarConfiguracaoUsuarioPorTenantUseCase useCase;

    @Mock
    private ConfiguracaoUsuarioRepository repository;

    @Test
    @DisplayName("Deve buscar configuração por tenantId com sucesso")
    void deveBuscarConfiguracaoPorTenantComSucesso() {
        var tenantId = "test-tenant-id";
        var configuracao = new ConfiguracaoUsuario("id-123", "https://example.com/logo.png", "Nome da Empresa", null);

        when(repository.findFirstBy()).thenReturn(Optional.of(configuracao));

        var result = useCase.execute(tenantId);

        assertNotNull(result);
        assertEquals("id-123", result.getId());
        assertEquals("https://example.com/logo.png", result.getLogoUrl());
        assertEquals("Nome da Empresa", result.getName());
        verify(repository, times(1)).findFirstBy();
    }

    @Test
    @DisplayName("Deve lançar exceção quando configuração não é encontrada")
    void deveLancarExcecaoQuandoConfiguracaoNaoEncontrada() {
        var tenantId = "tenant-inexistente";

        when(repository.findFirstBy()).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> useCase.execute(tenantId));
        verify(repository, times(1)).findFirstBy();
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é nulo")
    void deveLancarExcecaoQuandoTenantIdNulo() {
        assertThrows(ValidationException.class, () -> useCase.execute(null));
        verify(repository, never()).findFirstBy();
    }

    @Test
    @DisplayName("Deve lançar exceção quando tenantId é vazio")
    void deveLancarExcecaoQuandoTenantIdVazio() {
        assertThrows(ValidationException.class, () -> useCase.execute(""));
        verify(repository, never()).findFirstBy();
    }
}

