package com.flowbot.application;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class Example {

    private final RestClient restClient;

    Predicate<HttpStatusCode> isNotFound = HttpStatus.NOT_FOUND::equals;

    Predicate<HttpStatusCode> is5xx = HttpStatusCode::is5xxServerError;

    public Example(RestClient restClient) {
        this.restClient = restClient;
    }

    public void run() {
        shouldBeMakeSuccesfullyApiCall().join();
    }

    public CompletableFuture<String> shouldBeMakeSuccesfullyApiCall() {
        return CompletableFuture.supplyAsync(() -> restClient.post()
                        .uri("/poc/whats/generate-code")
                        .header(HttpHeaders.AUTHORIZATION, "x-api-key=1231243")
                        .retrieve()
                        .onStatus(isNotFound, notFoundHandler("def 1", "def 2"))
                        .onStatus(is5xx, a5xxHandler("def1", "def2"))
                        .body(String.class))
                .thenApply(body -> {
                    System.out.println(body);
                    return body;
                });
    }

    private ErrorHandler notFoundHandler(final String... args) {
        return (req, res) -> {
            throw new RuntimeException("not found handler imple......");
        };
    }

    private ErrorHandler a5xxHandler(final String... args) {
        return (req, res) -> {
            throw new RuntimeException("not found handler imple......");
        };
    }
}
