package com.flowbot.application.module.domain.campanha.useCase;

import com.flowbot.application.http.BotBuilderApi;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.module.domain.campanha.Campanha;
import com.flowbot.application.module.domain.campanha.CampanhaMongoDBRepository;
import com.flowbot.application.module.domain.numeros.Numero;
import com.flowbot.application.module.domain.numeros.NumeroMongoDbRepository;
import com.flowbot.application.module.domain.numeros.StatusNumero;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IniciarDisparosUseCase {

    private final CampanhaMongoDBRepository repository;
    private final BotBuilderApi botBuilderApi;
    private final NumeroMongoDbRepository numeroMongoDbRepository;

    public IniciarDisparosUseCase(CampanhaMongoDBRepository repository,
                                  BotBuilderApi botBuilderApi,
                                  NumeroMongoDbRepository numeroMongoDbRepository) {
        this.repository = repository;
        this.botBuilderApi = botBuilderApi;
        this.numeroMongoDbRepository = numeroMongoDbRepository;
    }

    public BatchSendResponse execute(String id) {
        var campanha = repository.findById(id).orElseThrow();
        var numero = numeroMongoDbRepository.findById(campanha.getNumeroIdRef()).orElseThrow();
        validarOperacao(campanha, numero);

        var body = buildBody(campanha.getNumerosParaDisparo(), numero.getWhatsappInternalId(), numero.getId());
        return botBuilderApi.batchSend(body);
    }

    private void validarOperacao(Campanha campanha, Numero numero) {
        if (Objects.isNull(campanha.getNumerosParaDisparo()) || campanha.getNumerosParaDisparo().isEmpty()) {
            throw new ValidationException("Campanha sem numeros para disparo");
        }

        if (!StatusNumero.VALIDADO.equals(numero.getStatusNumero())) {
            throw new ValidationException("Nao eh possivel continuar porque o numero esta com o status %s".formatted(numero.getStatusNumero().toString()));
        }
    }

    private HashMap<String, Object> buildBody(List<String> numerosParaDisparo, String whatsappInternalId, String id) {
        var map = new HashMap<String, Object>();
        map.put("to", numerosParaDisparo);
        map.put("key", whatsappInternalId);
        map.put("external_id", id);

        Map<String, Object> engine = new HashMap<>();
        engine.put("key", whatsappInternalId);
        engine.put("edges", Collections.emptyList());

        List<Map<String, Object>> nodesList = new ArrayList<>();
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", "node-0");
        nodeMap.put("type", "start");

        Map<String, Object> positionMap = new HashMap<>();
        positionMap.put("x", 0);
        positionMap.put("y", 0);
        nodeMap.put("position", positionMap);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("label", "teste");
        dataMap.put("phone", "48998457797");

        Map<String, Object> actionMap = new HashMap<>();
        actionMap.put("type", "ENVIAR_MENSAGEM");

        Map<String, Object> dataActionMap = new HashMap<>();
        dataActionMap.put("message", "envio de mensagem automatica em batch, nao responder");

        actionMap.put("data", dataActionMap);
        dataMap.put("action", actionMap);

        nodeMap.put("data", dataMap);
        nodeMap.put("width", 164);
        nodeMap.put("height", 72);

        nodesList.add(nodeMap);

        engine.put("nodes", nodesList);
        map.put("engine", engine);
        return map;
    }
}
