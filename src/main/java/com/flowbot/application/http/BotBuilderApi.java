package com.flowbot.application.http;

import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.http.dtos.VerifyNumberResponse;

import java.util.Map;

public interface BotBuilderApi {

    VerifyNumberResponse verificarStatusDoNumero(final String id);

    BatchSendResponse batchSend(Map<String, Object> payload);
}
