package com.flowbot.application.module.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flowbot.application.SecurityTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes de seguranca da aplicacao")
public class AuthTests extends SecurityTests {

    @Test
    @DisplayName("Nao deve permitir acessar os recursos pq nao esta autorizado")
    void deveReceber401() throws Exception {
        final var request = get("/numeros");

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deve permitir requisicao pois esta autorizado")
    void deveReceber200() throws Exception {
        final var token = super.getAcessToken();
        final var request = get("/numeros")
                .param("sortByNewest", "true")
                .param("terms", "teste")
                .with(req -> {
                    req.addHeader("Authorization", "Bearer " + token);
                    return req;
                });

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    void vapo() throws URISyntaxException, JsonProcessingException {
        String userAndGetToken = super.createUserAndGetToken("jena", "jean");
        System.out.println(userAndGetToken);
    }
}
