package com.flowbot.application.module.domain.numeros.api;

import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import com.flowbot.application.module.domain.numeros.api.dto.DtoUtils;
import com.flowbot.application.module.domain.numeros.api.dto.NumeroOutput;
import com.flowbot.application.module.domain.numeros.api.filter.GetNumerosFilter;
import com.flowbot.application.module.domain.numeros.useCase.BuscaNumerosUseCase;
import com.flowbot.application.module.domain.numeros.useCase.CriarNumeroUseCase;
import com.flowbot.application.module.domain.numeros.useCase.ValidarNumeroUseCase;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/numeros")
public class NumeroController {

    private final CriarNumeroUseCase criarNumeroUseCase;
    private final BuscaNumerosUseCase buscaNumerosUseCase;
    private final ValidarNumeroUseCase validarNumeroUseCase;
    private final ScheduledExecutorService scheduledExecutorService;

    public NumeroController(
            CriarNumeroUseCase criarNumeroUseCase,
            BuscaNumerosUseCase buscaNumerosUseCase,
            ValidarNumeroUseCase validarNumeroUseCase, ScheduledExecutorService scheduledExecutorService) {
        this.criarNumeroUseCase = criarNumeroUseCase;
        this.buscaNumerosUseCase = buscaNumerosUseCase;
        this.validarNumeroUseCase = validarNumeroUseCase;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @PostMapping("/validar/{id}")
    public void validarNumero(@PathVariable String id) {
        validarNumeroUseCase.execute(id);
    }

    @PostMapping
    public ResponseEntity<Void> criarNumero(@RequestBody final CriarNovoNumeroDto dto) {
        var numero = criarNumeroUseCase.execute(dto);

        var idNumero = numero.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.add("id", idNumero);

        scheduledExecutorService.schedule(
                () -> validarNumeroUseCase.execute(idNumero),
                30, TimeUnit.SECONDS);
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
