package com.flowbot.application.module.domain.campanha.apis;

import com.flowbot.application.module.domain.campanha.apis.dto.CriarCampanhaRequest;
import com.flowbot.application.module.domain.campanha.useCase.CriarCampanhaUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campanha")
public class CampanhaController {

    private final CriarCampanhaUseCase criarCampanhaUseCase;

    public CampanhaController(CriarCampanhaUseCase criarCampanhaUseCase) {
        this.criarCampanhaUseCase = criarCampanhaUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> criarCampanha(@RequestBody CriarCampanhaRequest request) {
        var campanha = criarCampanhaUseCase.execute(request);
        HttpHeaders headers = new HttpHeaders();

        headers.add("id", campanha.getId());
        return ResponseEntity.ok().headers(headers).build();
    }
}
