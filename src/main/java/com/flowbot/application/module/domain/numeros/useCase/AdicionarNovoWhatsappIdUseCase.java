package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AdicionarNovoWhatsappIdUseCase {
    private final NumeroMongoDbRepository repository;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ValidarNumeroUseCase validarNumeroUseCase;

    public AdicionarNovoWhatsappIdUseCase(
            NumeroMongoDbRepository repository,
            ScheduledExecutorService scheduledExecutorService,
            ValidarNumeroUseCase validarNumeroUseCase) {
        this.repository = repository;
        this.scheduledExecutorService = scheduledExecutorService;
        this.validarNumeroUseCase = validarNumeroUseCase;
    }

    public void execute(final String id, final String whatsappId) {
        var numero = repository.findById(id).orElseThrow();
        validarOperacao(numero, whatsappId);
        numero.atualizarWhatsappInternalId(whatsappId);
        repository.save(numero);

        scheduledExecutorService.schedule(() -> validarNumeroUseCase.execute(id), 30, TimeUnit.SECONDS);
    }

    private void validarOperacao(Numero numero, String whatsappId) {
        if (StatusNumero.BANIDO.equals(numero.getStatusNumero()))
            throw new ValidationException("Numero banido não pode ser alterado");

        if (Objects.isNull(whatsappId) || whatsappId.isBlank())
            throw new ValidationException("Whatsapp id não pode ser nulo");
    }
}
