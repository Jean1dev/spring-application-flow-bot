package com.flowbot.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        final var factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(30);

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
