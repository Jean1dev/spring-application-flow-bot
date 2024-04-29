package com.flowbot.application.module.domain.numeros.useCase;

import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.api.filter.GetNumerosFilter;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BuscaNumerosUseCase {

    private final MongoTemplate mongoTemplate;
    private final NumeroMongoDbRepository repository;

    public BuscaNumerosUseCase(MongoTemplate mongoTemplate, NumeroMongoDbRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    public Numero buscaPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ValidationException("Numero não encontrado"));
    }

    public Page<Numero> buscaPadrao(
            final GetNumerosFilter filter,
            final int page,
            final int size
    ) {
        var pageRequest = PageRequest.of(page, size);
        var query = buildQuery(filter);
        query.with(pageRequest);

        var numeros = mongoTemplate.find(query, Numero.class);
        return PageableExecutionUtils.getPage(
                numeros,
                pageRequest,
                () -> mongoTemplate.count(query, Numero.class)
        );
    }

    private Query buildQuery(GetNumerosFilter filter) {
        var query = new Query();
        if (Objects.nonNull(filter.getTerms()) && !filter.getTerms().isEmpty()) {
            query.addCriteria(Criteria.where("nick").regex(filter.getTerms() + ".*", "i"));
        }

        if (Objects.nonNull(filter.getStatus()) && !filter.getStatus().isEmpty()) {
            query.addCriteria(Criteria.where("statusNumero").is(filter.getStatus().toUpperCase()));
        }

        if (!filter.isSortByNewest()) {
            query.with(Sort.by(Sort.Direction.DESC, "dataCriacao"));
        }

        return query;
    }
}
