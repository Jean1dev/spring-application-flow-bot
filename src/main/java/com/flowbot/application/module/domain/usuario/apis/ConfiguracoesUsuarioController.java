package com.flowbot.application.module.domain.usuario.apis;

import com.flowbot.application.module.domain.usuario.ChavesPublicasDoUsuario;
import com.flowbot.application.module.domain.usuario.ChavesUsuarioTypeBot;
import com.flowbot.application.module.domain.usuario.apis.dto.AtualizarConfiguracaoUsuarioDto;
import com.flowbot.application.module.domain.usuario.apis.dto.ConfiguracaoUsuarioOutput;
import com.flowbot.application.module.domain.usuario.apis.dto.ConfiguracaoUsuarioPublicaDto;
import com.flowbot.application.module.domain.usuario.apis.dto.CriarConfiguracaoUsuarioDto;
import com.flowbot.application.module.domain.usuario.service.SegurancaUsuarioService;
import com.flowbot.application.module.domain.usuario.useCase.BuscarConfiguracaoUsuarioPorTenantUseCase;
import com.flowbot.application.module.domain.usuario.useCase.BuscarConfiguracaoUsuarioUseCase;
import com.flowbot.application.module.domain.usuario.useCase.SalvarConfiguracaoUsuarioUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracoes-usuario")
public class ConfiguracoesUsuarioController {
    private final SegurancaUsuarioService segurancaUsuarioService;
    private final SalvarConfiguracaoUsuarioUseCase salvarConfiguracaoUsuarioUseCase;
    private final BuscarConfiguracaoUsuarioUseCase buscarConfiguracaoUsuarioUseCase;
    private final BuscarConfiguracaoUsuarioPorTenantUseCase buscarConfiguracaoUsuarioPorTenantUseCase;

    public ConfiguracoesUsuarioController(
            SegurancaUsuarioService segurancaUsuarioService,
            SalvarConfiguracaoUsuarioUseCase salvarConfiguracaoUsuarioUseCase,
            BuscarConfiguracaoUsuarioUseCase buscarConfiguracaoUsuarioUseCase,
            BuscarConfiguracaoUsuarioPorTenantUseCase buscarConfiguracaoUsuarioPorTenantUseCase) {
        this.segurancaUsuarioService = segurancaUsuarioService;
        this.salvarConfiguracaoUsuarioUseCase = salvarConfiguracaoUsuarioUseCase;
        this.buscarConfiguracaoUsuarioUseCase = buscarConfiguracaoUsuarioUseCase;
        this.buscarConfiguracaoUsuarioPorTenantUseCase = buscarConfiguracaoUsuarioPorTenantUseCase;
    }

    @GetMapping("/chaves")
    public ChavesPublicasDoUsuario getKeys() {
        return segurancaUsuarioService.getKeys();
    }

    @GetMapping("/api-keys")
    public ChavesUsuarioTypeBot getApiKeys() {
        return segurancaUsuarioService.getApisKeys();
    }

    @PostMapping("/api-keys")
    public void postApiKeys(@RequestBody ChavesUsuarioTypeBot chavesUsuarioTypeBot) {
        segurancaUsuarioService.saveApiKeys(chavesUsuarioTypeBot);
    }

    @PostMapping
    public ConfiguracaoUsuarioOutput criar(@RequestBody CriarConfiguracaoUsuarioDto dto) {
        var configuracao = salvarConfiguracaoUsuarioUseCase.execute(dto);
        return ConfiguracaoUsuarioOutput.from(configuracao);
    }

    @GetMapping
    public ConfiguracaoUsuarioOutput buscar() {
        var configuracao = buscarConfiguracaoUsuarioUseCase.execute();
        return ConfiguracaoUsuarioOutput.from(configuracao);
    }

    @PutMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void atualizar(@RequestBody AtualizarConfiguracaoUsuarioDto dto) {
        salvarConfiguracaoUsuarioUseCase.execute(dto);
    }

    @GetMapping("/public/{tenant}")
    public ConfiguracaoUsuarioPublicaDto buscarPublica(@PathVariable String tenant) {
        var configuracao = buscarConfiguracaoUsuarioPorTenantUseCase.execute(tenant);
        return ConfiguracaoUsuarioPublicaDto.from(configuracao);
    }
}
