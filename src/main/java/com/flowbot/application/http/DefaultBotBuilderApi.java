package com.flowbot.application.http;

import com.flowbot.application.http.dtos.VerifyNumberResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;

import static com.flowbot.application.utils.HttpUtils.is5xx;
import static com.flowbot.application.utils.HttpUtils.isNotFound;

@Component
public class DefaultBotBuilderApi implements BotBuilderApi {
    private static final Logger log = LoggerFactory.getLogger(DefaultBotBuilderApi.class);
    private final RestClient restClient;

    public DefaultBotBuilderApi(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public VerifyNumberResponse verificarStatusDoNumero(String id) {
        var bodyMap = new HashMap<>();
        bodyMap.put("code", id);

        var responseSpec = restClient.post()
                .uri("/poc/whats/verify-number")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bodyMap)
                .retrieve()
                .onStatus(is5xx, a5xxHandler(""))
                .onStatus(isNotFound, notFoundHandler("def 1", "def 2"));

        var body = responseSpec.body(VerifyNumberResponse.class);
        return body;
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
