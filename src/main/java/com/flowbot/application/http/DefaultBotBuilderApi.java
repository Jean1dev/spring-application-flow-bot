package com.flowbot.application.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowbot.application.http.dtos.AuditMessagesResponse;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.http.dtos.VerifyNumberResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.flowbot.application.utils.HttpUtils.is5xx;
import static com.flowbot.application.utils.HttpUtils.isNotFound;

@Component
public class DefaultBotBuilderApi implements BotBuilderApi {
    private static final Logger log = LoggerFactory.getLogger(DefaultBotBuilderApi.class);
    private final RestClient restClient;
    private final ObjectMapper mapper;

    public DefaultBotBuilderApi(RestClient restClient, ObjectMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public VerifyNumberResponse verificarStatusDoNumero(String id) {
        if (Objects.isNull(id)) {
            return new VerifyNumberResponse(false);
        }

        var bodyMap = new HashMap<>();
        bodyMap.put("code", id);

        var responseSpec = restClient.post()
                .uri("/poc/whats/verify-number")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bodyMap)
                .retrieve()
                .onStatus(is5xx, a5xxHandler(""))
                .onStatus(isNotFound, notFoundHandler("def 1", "def 2"));

        return responseSpec.body(VerifyNumberResponse.class);
    }

    @Override
    public BatchSendResponse batchSend(Map<String, Object> payload) {
        try {
            var responseSpec = restClient.post()
                    .uri("/poc/whats/batch-send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(payload))
                    .retrieve()
                    .onStatus(is5xx, a5xxHandler(""))
                    .onStatus(isNotFound, notFoundHandler("def 1", "def 2"));

            var body = responseSpec.body(String.class);
            return new BatchSendResponse(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean playground(Map<String, Object> payload) {
        try {
            var responseSpec = restClient.post()
                    .uri("/poc/whats/playground-send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(payload))
                    .retrieve()
                    .onStatus(is5xx, a5xxHandler(""))
                    .onStatus(isNotFound, notFoundHandler("def 1", "def 2"));

            var body = responseSpec.body(Map.class);
            return (boolean) body.get("success");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuditMessagesResponse> audit(String key) {
        var responseSpec = restClient.get()
                .uri("/poc/whats/audit",
                        uriBuilder -> uriBuilder.queryParam("key", key).build())
                .retrieve()
                .onStatus(is5xx, a5xxHandler(""))
                .onStatus(isNotFound, notFoundHandler("def 1", "def 2"));

        return responseSpec.body(new ParameterizedTypeReference<>() {
        });
    }

    private RestClient.ResponseSpec.ErrorHandler notFoundHandler(final String... args) {
        return (req, res) -> {
            log.error(req.getURI().toString());
            log.error("Erro 400");
            throw new ValidationException("Falha ao atualizar o numero");
        };
    }

    private RestClient.ResponseSpec.ErrorHandler a5xxHandler(final String... args) {
        return (req, res) -> {
            log.error(req.getURI().toString());
            log.error("Erro 500");
            throw new ValidationException("Falha ao atualizar o numero");
        };
    }

}
