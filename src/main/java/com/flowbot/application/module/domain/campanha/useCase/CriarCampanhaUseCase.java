package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.campanha.CategoriaCampanha;
import com.flowbot.application.module.domain.campanha.apis.dto.CriarCampanhaRequest;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class CriarCampanhaUseCase {

    private final CampanhaMongoDBRepository repository;

    public CriarCampanhaUseCase(CampanhaMongoDBRepository repository) {
        this.repository = repository;
    }

    public Campanha execute(CriarCampanhaRequest request) {
        var campanha = new Campanha(
                null,
                request.titulo(),
                request.numeroIdRef(),
                request.numerosParaDisparo(),
                CategoriaCampanha.valueOf(request.categoria()),
                null,
                request.arquivosUrls(),
                request.messageDisparo(),
                request.flowIdRef()
        );

        var isValid = campanha.validate();
        if (!isValid) {
            var messages = String.join(", ", campanha.getValidateMessages());
            throw new ValidationException(messages);
        }

        return repository.save(campanha);
    }
}
