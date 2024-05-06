package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class ValidarNumeroUseCase {

    private final NumeroMongoDbRepository repository;
    private final BotBuilderApi botBuilderApi;

    public ValidarNumeroUseCase(NumeroMongoDbRepository repository, BotBuilderApi botBuilderApi) {
        this.repository = repository;
        this.botBuilderApi = botBuilderApi;
    }

    public void execute(final String id) {
        repository.findById(id)
                .ifPresent(numero -> {
                    validarSeDeveContinuar(numero);

                    var verifyNumberResponse = botBuilderApi.verificarStatusDoNumero(numero.getWhatsappInternalId());

                    if (verifyNumberResponse.connected()) {
                        numero.atualizarStatus(StatusNumero.VALIDADO);
                        repository.save(numero);
                    }

                });
    }

    private void validarSeDeveContinuar(Numero numero) {
        if (StatusNumero.CRIADO.equals(numero.getStatusNumero()) || StatusNumero.PENDENTE.equals(numero.getStatusNumero()))
            return;

        throw new ValidationException("Numero não pode ser validado, pois o status é " + numero.getStatusNumero().name() + " e não pode ser validado");
    }

}
