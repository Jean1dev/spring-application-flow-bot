package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.campanha.apis.dto.CampanhaOutput;
import com.flowbot.application.module.domain.campanha.apis.dto.DtoUtils;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscaCampanhaUseCase {

    private final CampanhaMongoDBRepository repository;
    private final NumeroMongoDbRepository numeroMongoDbRepository;

    public BuscaCampanhaUseCase(CampanhaMongoDBRepository repository, NumeroMongoDbRepository numeroMongoDbRepository) {
        this.repository = repository;
        this.numeroMongoDbRepository = numeroMongoDbRepository;
    }

    public Page<Campanha> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<CampanhaOutput> convertToOutput(List<Campanha> campanhas) {
        return campanhas
                .stream()
                .map(campanha -> {
                    if (campanha.getNumeroIdRef() == null) {
                        return DtoUtils.toOutput(campanha);
                    }

                    var numero = numeroMongoDbRepository.findById(campanha.getNumeroIdRef());
                    return numero
                            .map(value -> DtoUtils.toOutput(campanha, value))
                            .orElseGet(() -> DtoUtils.toOutput(campanha));

                })
                .toList();
    }
}
