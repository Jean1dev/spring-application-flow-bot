package com.flowbot.application.http;

import com.flowbot.application.http.dtos.VerifyNumberResponse;

public interface BotBuilderApi {

    VerifyNumberResponse verificarStatusDoNumero(final String id);
}
