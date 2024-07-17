package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.TypeBotAddInput;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.VincularTypeBotInput;
import com.flowbot.application.module.domain.numeros.service.RelacionamentoNumeroTypeBotService;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class VincularTypeBotComNumeroUseCase {
    private final BotBuilderApi botBuilderApi;
    private final NumeroMongoDbRepository repository;
    private final RelacionamentoNumeroTypeBotService relacionamentoNumeroTypeBotService;

    public VincularTypeBotComNumeroUseCase(
            BotBuilderApi botBuilderApi,
            NumeroMongoDbRepository repository,
            RelacionamentoNumeroTypeBotService relacionamentoNumeroTypeBotService) {
        this.botBuilderApi = botBuilderApi;
        this.repository = repository;
        this.relacionamentoNumeroTypeBotService = relacionamentoNumeroTypeBotService;
    }

    public void execute(VincularTypeBotInput input) {
        repository.findById(input.numeroId())
                .ifPresentOrElse(
                        (numero) -> vincular(numero, input),
                        () -> {
                            throw new ValidationException("Numero não encontrado");
                        });
    }

    private void vincular(Numero numero, VincularTypeBotInput input) {
        validar(numero, input);
        var typeBotAddInput = new TypeBotAddInput(numero.getWhatsappInternalId(), input.apiHost(), input.typebotName());
        var result = botBuilderApi.addTypeBot(typeBotAddInput);
        if (!result) {
            throw new ValidationException("Falha ao vincular o typebot");
        }

        relacionamentoNumeroTypeBotService.relacionar(input.numeroId(), input.typebotName(), input.apiHost());
    }

    private void validar(Numero numero, VincularTypeBotInput input) {
        if (!StatusNumero.VALIDADO.equals(numero.getStatusNumero())) {
            throw new ValidationException("Numero não está validado");
        }

        if (numero.getWhatsappInternalId() == null || numero.getWhatsappInternalId().isEmpty()) {
            throw new ValidationException("Numero não possui whatsappInternalId");
        }

        if (input.typebotName() == null || input.typebotName().isEmpty()) {
            throw new ValidationException("TypebotName é obrigatório");
        }
    }

}
