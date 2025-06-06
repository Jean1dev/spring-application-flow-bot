package com.flowbot.application.module.domain.financeiro.assinaturas.api;

import com.flowbot.application.module.domain.financeiro.assinaturas.PeriodoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AcessoOutputDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.CriarPlanoInputDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.RegistarAcessoDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.useCase.CriarPlanoUseCase;
import com.flowbot.application.module.domain.financeiro.assinaturas.useCase.GerenciamentoDoPlanoUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plano")
public class PlanoController {

    private final CriarPlanoUseCase criarPlanoUseCase;
    private final GerenciamentoDoPlanoUseCase gerenciamentoDoPlanoUseCase;

    public PlanoController(CriarPlanoUseCase criarPlanoUseCase,
                           GerenciamentoDoPlanoUseCase gerenciamentoDoPlanoUseCase) {
        this.criarPlanoUseCase = criarPlanoUseCase;
        this.gerenciamentoDoPlanoUseCase = gerenciamentoDoPlanoUseCase;
    }

    @GetMapping("/vigente")
    public PlanoAtivoOutput obterPlanoVigente(@RequestParam String email) {
        return gerenciamentoDoPlanoUseCase.obterDadosPlano(email);
    }

    @PostMapping
    public ResponseEntity<Void> criarPlano(@RequestBody CriarPlanoInputDto dto) {
        PeriodoPlano periodoPlano = PeriodoPlano.valueOf(dto.periodoPlano());
        var id = criarPlanoUseCase.criarPlanoSimples(dto.email(), periodoPlano);
        HttpHeaders headers = new HttpHeaders();
        headers.add("id", id);
        return ResponseEntity.ok().headers(headers).build();
    }

    @GetMapping
    public List<PlanoAtivoOutput> get() {
        return gerenciamentoDoPlanoUseCase.listPlanosAtivos();
    }

    @PostMapping("/acesso")
    public AcessoOutputDto registarAcesso(@RequestBody RegistarAcessoDto body) {
        return gerenciamentoDoPlanoUseCase.registarAcesso(body);
    }
}
