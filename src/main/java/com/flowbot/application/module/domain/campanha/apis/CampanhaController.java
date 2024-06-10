package com.flowbot.application.module.domain.campanha.apis;

import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.apis.dto.CampanhaOutput;
import com.flowbot.application.module.domain.campanha.apis.dto.CriarCampanhaRequest;
import com.flowbot.application.module.domain.campanha.useCase.BuscaCampanhaUseCase;
import com.flowbot.application.module.domain.campanha.useCase.CriarCampanhaUseCase;
import com.flowbot.application.module.domain.campanha.useCase.IniciarDisparosUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campanhas")
public class CampanhaController {

    private final CriarCampanhaUseCase criarCampanhaUseCase;
    private final IniciarDisparosUseCase iniciarDisparosUseCase;
    private final BuscaCampanhaUseCase buscaCampanhaUseCase;

    public CampanhaController(CriarCampanhaUseCase criarCampanhaUseCase,
                              IniciarDisparosUseCase iniciarDisparosUseCase,
                              BuscaCampanhaUseCase buscaCampanhaUseCase) {
        this.criarCampanhaUseCase = criarCampanhaUseCase;
        this.iniciarDisparosUseCase = iniciarDisparosUseCase;
        this.buscaCampanhaUseCase = buscaCampanhaUseCase;
    }

    @GetMapping
    public Page<CampanhaOutput> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Campanha> campanhaPage = buscaCampanhaUseCase.findAll(page, size);
        Pageable pageable = campanhaPage.getPageable();
        var results = buscaCampanhaUseCase.convertToOutput(campanhaPage.getContent());

        return new PageImpl<>(results, pageable, campanhaPage.getTotalElements());
    }

    @PostMapping
    public ResponseEntity<Void> criarCampanha(@RequestBody CriarCampanhaRequest request) {
        var campanha = criarCampanhaUseCase.execute(request);
        HttpHeaders headers = new HttpHeaders();

        headers.add("id", campanha.getId());
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "id");
        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/disparar/{id}")
    public BatchSendResponse dispararCampanha(@PathVariable String id) {
        return iniciarDisparosUseCase.execute(id);
    }
}
