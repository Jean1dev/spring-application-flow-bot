package com.flowbot.application.module.domain.numeros.api;

import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import com.flowbot.application.module.domain.numeros.api.dto.DtoUtils;
import com.flowbot.application.module.domain.numeros.api.dto.NumeroOutput;
import com.flowbot.application.module.domain.numeros.api.filter.GetNumerosFilter;
import com.flowbot.application.module.domain.numeros.useCase.BuscaNumerosUseCase;
import com.flowbot.application.module.domain.numeros.useCase.CriarNumeroUseCase;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/numeros")
public class NumeroController {

    private final CriarNumeroUseCase criarNumeroUseCase;
    private final BuscaNumerosUseCase buscaNumerosUseCase;

    public NumeroController(CriarNumeroUseCase criarNumeroUseCase, BuscaNumerosUseCase buscaNumerosUseCase) {
        this.criarNumeroUseCase = criarNumeroUseCase;
        this.buscaNumerosUseCase = buscaNumerosUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> criarNumero(@RequestBody final CriarNovoNumeroDto dto) {
        var numero = criarNumeroUseCase.execute(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("id", numero.getId());
        return ResponseEntity.ok().headers(headers).build();
    }

    @GetMapping
    public Page<NumeroOutput> listar(
            GetNumerosFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return DtoUtils.toDto(buscaNumerosUseCase.buscaPadrao(filter, page, size));
    }

    @GetMapping("/{id}")
    public Numero buscarPorId(@PathVariable String id) {
        return buscaNumerosUseCase.buscaPorId(id);
    }
}
