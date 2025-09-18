package com.flowbot.application.module.domain.financeiro.assinaturas.api;

import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AnomaliaAcessoDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.useCase.DetectorAnomaliaAcessoUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/anomalias-acesso")
public class AnomaliaAcessoController {

    private final DetectorAnomaliaAcessoUseCase detectorAnomaliaAcessoUseCase;

    public AnomaliaAcessoController(DetectorAnomaliaAcessoUseCase detectorAnomaliaAcessoUseCase) {
        this.detectorAnomaliaAcessoUseCase = detectorAnomaliaAcessoUseCase;
    }

    @GetMapping
    public List<AnomaliaAcessoDto> detectarTodasAnomalias() {
        return detectorAnomaliaAcessoUseCase.detectarAnomalias();
    }

    @GetMapping("/periodo")
    public List<AnomaliaAcessoDto> detectarAnomaliasPorPeriodo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth fim) {
        var inicioDateTime = inicio.atDay(1).atStartOfDay();
        var fimDateTime = fim.atEndOfMonth().atTime(23, 59, 59);
        return detectorAnomaliaAcessoUseCase.detectarAnomaliasPorPeriodo(inicioDateTime, fimDateTime);
    }

    @GetMapping("/criticas")
    public List<AnomaliaAcessoDto> detectarAnomaliasCriticas() {
        return detectorAnomaliaAcessoUseCase.detectarAnomalias()
                .stream()
                .filter(anomalia -> anomalia.nivelSuspeita() == AnomaliaAcessoDto.NivelSuspeita.CRITICO)
                .toList();
    }

    @GetMapping("/suspeitas")
    public List<AnomaliaAcessoDto> detectarAnomaliasSuspeitas() {
        return detectorAnomaliaAcessoUseCase.detectarAnomalias()
                .stream()
                .filter(anomalia -> anomalia.nivelSuspeita().ordinal() >= AnomaliaAcessoDto.NivelSuspeita.ALTO.ordinal())
                .toList();
    }
}
