package com.flowbot.application.http;

import com.flowbot.application.http.dtos.AuditMessagesResponse;
import com.flowbot.application.http.dtos.BatchSendResponse;
import com.flowbot.application.http.dtos.TypeBotAddInput;
import com.flowbot.application.http.dtos.VerifyNumberResponse;

import java.util.List;
import java.util.Map;

public interface BotBuilderApi {

    VerifyNumberResponse verificarStatusDoNumero(final String id);

    BatchSendResponse batchSend(Map<String, Object> payload);

    boolean playground(Map<String, Object> payload);

    List<AuditMessagesResponse> audit(String key);

    boolean addTypeBot(TypeBotAddInput addInput);
}
