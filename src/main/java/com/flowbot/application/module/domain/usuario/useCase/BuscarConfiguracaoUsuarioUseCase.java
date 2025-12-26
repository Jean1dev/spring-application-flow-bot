package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class BuscarConfiguracaoUsuarioUseCase {

    private final ConfiguracaoUsuarioRepository repository;

    public BuscarConfiguracaoUsuarioUseCase(ConfiguracaoUsuarioRepository repository) {
        this.repository = repository;
    }

    public ConfiguracaoUsuario execute() {
        var tenantId = TenantThreads.getTenantId();

        if (tenantId == null || tenantId.isEmpty()) {
            throw new ValidationException("Tenant ID não pode ser nulo ou vazio");
        }

        return repository.findFirstBy()
                .orElseThrow(() -> new ValidationException("Configuração não encontrada para o usuário"));
    }
}

