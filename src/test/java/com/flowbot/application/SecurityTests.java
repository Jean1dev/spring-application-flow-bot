package com.flowbot.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("prd")
@Tag("security-tests")
public abstract class SecurityTests {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    protected static KeycloakContainer keycloakContainer;

    static {
        keycloakContainer = new KeycloakContainer().withRealmImportFile("config/kc-realm.json");
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/tests-realm");
        registry.add("keycloak.enabled", () -> true);
    }

    protected String getAcessToken() throws URISyntaxException {
        return generateAuthToken("testador@testador.com", "s3cr3t");
    }

    private String generateAuthToken(String username, String password) throws URISyntaxException {
        URI authorizationURI = new URIBuilder(keycloakContainer.getAuthServerUrl() + "/realms/tests-realm/protocol/openid-connect/token").build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("client-tests"));
        formData.put("username", Collections.singletonList(username));
        formData.put("password", Collections.singletonList(password));

        var restClient = RestClient.builder().build();

        var body = restClient
                .post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(LinkedHashMap.class);

        return body.get("access_token").toString();
    }

    protected String createUserAndGetToken(String username, String password) throws URISyntaxException, JsonProcessingException {
        URI authorizationURI = new URIBuilder(keycloakContainer.getAuthServerUrl() + "/admin/realms/tests-realm/users").build();

        var payload = new HashMap<>();
        payload.put("firstName", "firstName" + username);
        payload.put("lastName", "lastName" + username);
        payload.put("email", username + "@email.com");
        payload.put("username", username);
        payload.put("enabled", true);

        var credentiails = new HashMap<>();
        credentiails.put("type", "password");
        credentiails.put("value", password);
        credentiails.put("temporary", false);

        payload.put("credentials", Collections.singletonList(credentiails));

        var restClient = RestClient.builder().build();

        var accessTokenString = keycloakContainer
                .getKeycloakAdminClient()
                .tokenManager()
                .getAccessTokenString();

        restClient
                .post()
                .uri(authorizationURI)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessTokenString)
                .body(mapper.writeValueAsString(payload))
                .retrieve();

        return generateAuthToken(username, password);
    }
}
