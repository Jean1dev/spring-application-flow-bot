package com.flowbot.application.module.domain.usuario.apis;

import com.flowbot.application.context.TenantThreads;
import com.flowbot.application.module.domain.usuario.apis.dto.TenantDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @GetMapping("/tenant")
    public TenantDto obterTenant() {
        var tenantId = TenantThreads.getTenantId();
        return new TenantDto(tenantId);
    }
}

