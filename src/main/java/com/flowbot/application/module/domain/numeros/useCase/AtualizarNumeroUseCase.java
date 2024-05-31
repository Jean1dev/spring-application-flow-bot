package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.api.dto.AtualizarNumeroInput;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class AtualizarNumeroUseCase {

    private final NumeroMongoDbRepository repository;

    public AtualizarNumeroUseCase(NumeroMongoDbRepository repository) {
        this.repository = repository;
    }

    public void execute(final String id, final AtualizarNumeroInput input) {
        validarInput(input);
        var oldNumero = repository.findById(id).orElseThrow();
        oldNumero.atualizarNumero(input.numero(), input.apelido());
        repository.save(oldNumero);
    }

    private void validarInput(AtualizarNumeroInput input) {
        if (input == null) throw new ValidationException("Input não pode ser nulo");

        if (input.apelido() == null || input.apelido().isBlank())
            throw new ValidationException("Apelido não pode ser em branco ou nulo");

        if (input.numero() == null || input.numero().isBlank())
            throw new ValidationException("Numero numero não pode ser em branco ou nulo");
    }
}
