package com.flowbot.application.module.domain.playground.useCase;

import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import com.flowbot.application.module.domain.numeros.useCase.CriarNumeroUseCase;
import com.flowbot.application.module.domain.numeros.useCase.ValidarNumeroUseCase;
import com.flowbot.application.module.domain.playground.api.dto.PlayGroundExecOutput;
import com.flowbot.application.shared.ApplicationScheduleService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.flowbot.application.utils.Utils.formatPhone;
import static com.flowbot.application.utils.Utils.generateWhatsappId;

@Service
public class PlayGroundUseCase {

    private final BotBuilderApi botBuilderApi;
    private final NumeroMongoDbRepository numeroMongoDbRepository;
    private final CriarNumeroUseCase criarNumeroUseCase;
    private final ValidarNumeroUseCase validarNumeroUseCase;
    private final ApplicationScheduleService scheduledExecutorService;

    public PlayGroundUseCase(BotBuilderApi botBuilderApi,
                             NumeroMongoDbRepository numeroMongoDbRepository,
                             CriarNumeroUseCase criarNumeroUseCase,
                             ValidarNumeroUseCase validarNumeroUseCase,
                             ApplicationScheduleService scheduledExecutorService) {
        this.botBuilderApi = botBuilderApi;
        this.numeroMongoDbRepository = numeroMongoDbRepository;
        this.criarNumeroUseCase = criarNumeroUseCase;
        this.validarNumeroUseCase = validarNumeroUseCase;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public PlayGroundExecOutput execute(
            final String senderId,
            final String recipientNumber,
            final String message
    ) {
        Optional<Numero> optionalNumero = numeroMongoDbRepository.findById(senderId);

        if (optionalNumero.isPresent()) {
            var numero = optionalNumero.get();

            if (StatusNumero.VALIDADO != numero.getStatusNumero()) {
                validarNumero(numero.getId());
                return new PlayGroundExecOutput(false, true, "Numero não está validado", numero.getId());
            }

            var body = buildBody(numero.getWhatsappInternalId(), formatPhone(recipientNumber), message);
            boolean playgrounded = botBuilderApi.playground(body);
            return new PlayGroundExecOutput(playgrounded, false, getMessage(playgrounded), numero.getId());
        } else {
            var id = criarNovoNumero();
            validarNumero(id);
            return new PlayGroundExecOutput(false, true, "Numero nao encontrado, vinculo o whatsapp", id);
        }
    }

    private void validarNumero(String id) {
        scheduledExecutorService.schedule(
                () -> validarNumeroUseCase.execute(id),
                30, TimeUnit.SECONDS);
    }

    private String getMessage(boolean playgrounded) {
        return playgrounded
                ? "Playground enviado com sucesso"
                : "Falha ao enviar o playground";
    }

    private HashMap<String, Object> buildBody(String whatsappInternalId, String recipientNumber, String message) {
        var map = new HashMap<String, Object>();
        map.put("instanceKey", whatsappInternalId);
        map.put("recipient", recipientNumber);
        map.put("textMessage", message);
        return map;
    }

    private String criarNovoNumero() {
        var dto = new CriarNovoNumeroDto("playground sender", "numero playground", generateWhatsappId(12));
        Numero numero = criarNumeroUseCase.execute(dto);
        return numero.getId();
    }
}
