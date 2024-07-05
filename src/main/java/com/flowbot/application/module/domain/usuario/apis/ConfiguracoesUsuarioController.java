package com.flowbot.application.module.domain.usuario.apis;

import com.flowbot.application.module.domain.usuario.ChavesUsuarioTypeBot;
import com.flowbot.application.module.domain.usuario.ChavesPublicasDoUsuario;
import com.flowbot.application.module.domain.usuario.service.SegurancaUsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracoes-usuario")
public class ConfiguracoesUsuarioController {
    private final SegurancaUsuarioService segurancaUsuarioService;

    public ConfiguracoesUsuarioController(SegurancaUsuarioService segurancaUsuarioService) {
        this.segurancaUsuarioService = segurancaUsuarioService;
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
}
