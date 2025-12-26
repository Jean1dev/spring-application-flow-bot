package com.flowbot.application.module.domain.usuario.useCase;

import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuario;
import com.flowbot.application.module.domain.usuario.ConfiguracaoUsuarioRepository;
import com.flowbot.application.module.domain.usuario.apis.dto.AtualizarConfiguracaoUsuarioDto;
import com.flowbot.application.module.domain.usuario.apis.dto.CriarConfiguracaoUsuarioDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class SalvarConfiguracaoUsuarioUseCase {

    private final ConfiguracaoUsuarioRepository repository;

    public SalvarConfiguracaoUsuarioUseCase(ConfiguracaoUsuarioRepository repository) {
        this.repository = repository;
    }

    public ConfiguracaoUsuario execute(CriarConfiguracaoUsuarioDto dto) {
        return salvar(dto.logoUrl(), dto.name());
    }

    public ConfiguracaoUsuario execute(AtualizarConfiguracaoUsuarioDto dto) {
        return salvar(dto.logoUrl(), dto.name());
    }

    private ConfiguracaoUsuario salvar(String logoUrl, String name) {
        var tenantId = TenantThreads.getTenantId();

        if (tenantId == null || tenantId.isEmpty()) {
            throw new ValidationException("Tenant ID não pode ser nulo ou vazio");
        }

        var configuracaoExistente = repository.findFirstBy();
        
        if (configuracaoExistente.isPresent()) {
            var configuracao = configuracaoExistente.get();
            configuracao.atualizar(logoUrl, name);
            return repository.save(configuracao);
        }

        var novaConfiguracao = new ConfiguracaoUsuario(
                null,
                logoUrl,
                name,
                null
        );

        return repository.save(novaConfiguracao);
    }
}

