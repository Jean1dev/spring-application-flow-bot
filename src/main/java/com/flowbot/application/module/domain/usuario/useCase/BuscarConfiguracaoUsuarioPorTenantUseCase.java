package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class BuscarConfiguracaoUsuarioPorTenantUseCase {

    private final ConfiguracaoUsuarioRepository repository;

    public BuscarConfiguracaoUsuarioPorTenantUseCase(ConfiguracaoUsuarioRepository repository) {
        this.repository = repository;
    }

    public ConfiguracaoUsuario execute(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            throw new ValidationException("Tenant ID não pode ser nulo ou vazio");
        }

        var tenantAnterior = TenantThreads.getTenantId();
        try {
            TenantThreads.setTenantId(tenantId);
            return repository.findFirstBy()
                    .orElseThrow(() -> new ValidationException("Configuração não encontrada para o tenant informado"));
        } finally {
            if (tenantAnterior != null && !tenantAnterior.isEmpty()) {
                TenantThreads.setTenantId(tenantAnterior);
            } else {
                TenantThreads.clear();
            }
        }
    }
}

