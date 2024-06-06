package com.flowbot.application.module.domain.campanha.apis;

import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.apis.dto.CriarCampanhaRequest;
import com.flowbot.application.module.domain.campanha.useCase.CriarCampanhaUseCase;
import com.flowbot.application.module.domain.campanha.useCase.IniciarDisparosUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campanhas")
public class CampanhaController {

    private final CriarCampanhaUseCase criarCampanhaUseCase;
    private final IniciarDisparosUseCase iniciarDisparosUseCase;

    public CampanhaController(CriarCampanhaUseCase criarCampanhaUseCase,
                              IniciarDisparosUseCase iniciarDisparosUseCase) {
        this.criarCampanhaUseCase = criarCampanhaUseCase;
        this.iniciarDisparosUseCase = iniciarDisparosUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> criarCampanha(@RequestBody CriarCampanhaRequest request) {
        var campanha = criarCampanhaUseCase.execute(request);
        HttpHeaders headers = new HttpHeaders();

        headers.add("id", campanha.getId());
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/disparar/{id}")
    public BatchSendResponse dispararCampanha(@PathVariable String id) {
        return iniciarDisparosUseCase.execute(id);
    }
}
