package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.module.domain.financeiro.assinaturas.PeriodoPlano;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoMongoDbRepository;
import org.springframework.stereotype.Service;

@Service
public class CriarPlanoUseCase {
    private final PlanoMongoDbRepository  planoMongoDbRepository;

    public CriarPlanoUseCase(PlanoMongoDbRepository planoMongoDbRepository) {
        this.planoMongoDbRepository = planoMongoDbRepository;
    }

    public String criarPlanoSimples(String email, PeriodoPlano periodoPlano) {
        var plano = Plano.criarPlanoPadrao(email, periodoPlano);
        return planoMongoDbRepository.save(plano).getId();
    }
}
