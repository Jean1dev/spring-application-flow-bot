package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverCampanhaUseCase {
    private final CampanhaMongoDBRepository  repository;

    public RemoverCampanhaUseCase(CampanhaMongoDBRepository repository) {
        this.repository = repository;
    }

    public void execute(String id) {
        repository.deleteById(id);
    }
}
