package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.api.dto.CriarNovoNumeroDto;
import org.springframework.stereotype.Service;

@Service
public class CriarNumeroUseCase {

    private final NumeroMongoDbRepository repository;

    public CriarNumeroUseCase(NumeroMongoDbRepository repository) {
        this.repository = repository;
    }

    public Numero execute(final CriarNovoNumeroDto dto) {
        var numero = new Numero(
                null,
                dto.nick(),
                null,
                null,
                dto.numero(),
                null
        );

        return repository.save(numero);
    }
}
