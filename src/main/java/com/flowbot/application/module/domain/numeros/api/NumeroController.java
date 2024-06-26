package com.flowbot.application.module.domain.numeros.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.*;
import com.flowbot.application.module.domain.numeros.api.filter.GetNumerosFilter;
import com.flowbot.application.module.domain.numeros.useCase.*;
import com.flowbot.application.shared.ApplicationScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/numeros")
public class NumeroController {

    private final CriarNumeroUseCase criarNumeroUseCase;
    private final BuscaNumerosUseCase buscaNumerosUseCase;
    private final ValidarNumeroUseCase validarNumeroUseCase;
    private final ApplicationScheduleService scheduledExecutorService;
    private final AdicionarNovoWhatsappIdUseCase adicionarNovoWhatsappIdUseCase;
    private final AtualizarNumeroUseCase atualizarNumeroUseCase;

    public NumeroController(
            CriarNumeroUseCase criarNumeroUseCase,
            BuscaNumerosUseCase buscaNumerosUseCase,
            ValidarNumeroUseCase validarNumeroUseCase,
            ApplicationScheduleService scheduledExecutorService,
            AdicionarNovoWhatsappIdUseCase adicionarNovoWhatsappIdUseCase,
            AtualizarNumeroUseCase atualizarNumeroUseCase) {
        this.criarNumeroUseCase = criarNumeroUseCase;
        this.buscaNumerosUseCase = buscaNumerosUseCase;
        this.validarNumeroUseCase = validarNumeroUseCase;
        this.scheduledExecutorService = scheduledExecutorService;
        this.adicionarNovoWhatsappIdUseCase = adicionarNovoWhatsappIdUseCase;
        this.atualizarNumeroUseCase = atualizarNumeroUseCase;
    }

    @PutMapping("/{id}")
    public void adicionarNovoWhatsappId(@PathVariable String id, @RequestBody JsonNode body) {
        var whatsappId = body.get("whatsappId").asText();
        adicionarNovoWhatsappIdUseCase.execute(id, whatsappId);
    }

    @PostMapping("/validar/{id}")
    public void validarNumero(@PathVariable String id) {
        validarNumeroUseCase.execute(id);
    }

    @PutMapping("atualizar/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void atualizarNumero(@PathVariable String id, @RequestBody AtualizarNumeroInput input) {
        atualizarNumeroUseCase.execute(id, input);

        scheduledExecutorService.schedule(
                () -> validarNumeroUseCase.execute(id),
                30, TimeUnit.SECONDS);
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

    @GetMapping("/simplificado")
    public List<NumeroSimplificadoOutput> listarSimplificado(
    ) {
        return DtoUtils.listToDtoSimplificado(buscaNumerosUseCase.buscaTodos());
    }

    @GetMapping("/simplificado/validado")
    public List<NumeroSimplificadoOutput> listarSimplificadoValidado(
    ) {
        return DtoUtils.listToDtoSimplificado(buscaNumerosUseCase.buscaPorStatus(StatusNumero.VALIDADO));
    }

    @GetMapping("/{id}")
    public Numero buscarPorId(@PathVariable String id) {
        return buscaNumerosUseCase.buscaPorId(id);
    }
}
