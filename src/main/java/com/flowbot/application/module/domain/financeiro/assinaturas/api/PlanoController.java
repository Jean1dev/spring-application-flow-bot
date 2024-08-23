package com.flowbot.application.module.domain.financeiro.assinaturas.api;

import com.flowbot.application.module.domain.financeiro.assinaturas.PeriodoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.CriarPlanoInputDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.useCase.CriarPlanoUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plano")
public class PlanoController {

    private final CriarPlanoUseCase criarPlanoUseCase;

    public PlanoController(CriarPlanoUseCase criarPlanoUseCase) {
        this.criarPlanoUseCase = criarPlanoUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> criarPlano(@RequestBody CriarPlanoInputDto dto) {
        PeriodoPlano periodoPlano = PeriodoPlano.valueOf(dto.periodoPlano());
        var id = criarPlanoUseCase.criarPlanoSimples(dto.email(), periodoPlano);
        HttpHeaders headers = new HttpHeaders();
        headers.add("id", id);
        return ResponseEntity.ok().headers(headers).build();
    }
}
