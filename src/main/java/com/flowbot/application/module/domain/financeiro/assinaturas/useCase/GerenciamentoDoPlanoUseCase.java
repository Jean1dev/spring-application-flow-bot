package com.flowbot.application.module.domain.financeiro.assinaturas.useCase;

import com.flowbot.application.module.domain.financeiro.assinaturas.Acesso;
import com.flowbot.application.module.domain.financeiro.assinaturas.Plano;
import com.flowbot.application.module.domain.financeiro.assinaturas.PlanoAtivoOutput;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.AcessoOutputDto;
import com.flowbot.application.module.domain.financeiro.assinaturas.api.dto.RegistarAcessoDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GerenciamentoDoPlanoUseCase {
    private final MongoTemplate mongoTemplate;

    public GerenciamentoDoPlanoUseCase(@Qualifier("adminMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<PlanoAtivoOutput> listPlanosAtivos() {
        return mongoTemplate.findAll(Plano.class)
                .stream()
                .map(PlanoAtivoOutput::map)
                .toList();
    }

    public PlanoAtivoOutput obterDadosPlano(String email) {
        return mongoTemplate.findAll(Plano.class)
                .stream()
                .filter(plano -> plano.getUsuario().email().equals(email))
                .findFirst()
                .map(PlanoAtivoOutput::map)
                .orElseThrow();
    }

    public AcessoOutputDto registarAcesso(RegistarAcessoDto registarAcessoDto) {
        var plano = getPlanoById(registarAcessoDto.email());
        var isFirstAccess = isFirstAccess(plano.getId());
        var acesso = new Acesso(registarAcessoDto.fonte(), registarAcessoDto.localizacao(), plano.getId());

        mongoTemplate.save(acesso);
        return new AcessoOutputDto(isFirstAccess, PlanoAtivoOutput.map(plano));
    }

    private Plano getPlanoById(String email) {
        var query = new Query().addCriteria(Criteria.where("usuario.email").is(email));
        return mongoTemplate.find(query, Plano.class)
                .stream()
                .findFirst()
                .orElseThrow();
    }

    private boolean isFirstAccess(String planoRef) {
        return mongoTemplate.findAll(Acesso.class)
                .stream()
                .noneMatch(acesso -> acesso.getPlanoRef().equals(planoRef));
    }
}
