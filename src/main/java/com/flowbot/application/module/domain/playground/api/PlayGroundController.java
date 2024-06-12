package com.flowbot.application.module.domain.playground.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowbot.application.module.domain.playground.api.dto.PlayGroundExecOutput;
import com.flowbot.application.module.domain.playground.useCase.PlayGroundUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playground")
public class PlayGroundController {

    private final PlayGroundUseCase useCase;

    public PlayGroundController(PlayGroundUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public PlayGroundExecOutput execute(@RequestBody JsonNode json) {
        String senderId = json.get("senderId").asText();
        String recipientNumber = json.get("recipientNumber").asText();
        String message = json.get("message").asText();

        return useCase.execute(senderId, recipientNumber, message);
    }
}
