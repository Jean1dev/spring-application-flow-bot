package com.flowbot.application.module.domain.numeros.api;

import com.flowbot.application.module.domain.numeros.api.dto.VincularTypeBotInput;
import com.flowbot.application.module.domain.numeros.service.RelacionamentoNumeroTypeBotService;
import com.flowbot.application.module.domain.numeros.useCase.VincularTypeBotComNumeroUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/numeros-typebots")
public class NumerosTypeBotController {
    private final VincularTypeBotComNumeroUseCase vincularTypeBotComNumeroUseCase;
    private final RelacionamentoNumeroTypeBotService relacionamentoNumeroTypeBotService;

    public NumerosTypeBotController(
            VincularTypeBotComNumeroUseCase vincularTypeBotComNumeroUseCase,
            RelacionamentoNumeroTypeBotService relacionamentoNumeroTypeBotService
    ) {
        this.vincularTypeBotComNumeroUseCase = vincularTypeBotComNumeroUseCase;
        this.relacionamentoNumeroTypeBotService = relacionamentoNumeroTypeBotService;
    }

    @PostMapping("/vincular")
    public void vincular(@RequestBody VincularTypeBotInput input) {
        vincularTypeBotComNumeroUseCase.execute(input);
    }

    @GetMapping("/quantidade-vinculos")
    public Map quantidadeVinculos(@RequestParam("name") String name, @RequestParam("apiHost") String apiHost) {
        return Map.of("quantidade",
                relacionamentoNumeroTypeBotService.quantidadeDeNumerosVinculadosNoTypebot(name, apiHost));
    }
}
