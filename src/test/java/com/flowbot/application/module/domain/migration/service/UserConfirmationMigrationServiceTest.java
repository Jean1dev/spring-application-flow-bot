package com.flowbot.application.module.domain.migration.service;

import com.flowbot.application.UseCaseTest;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserConfirmationMigrationServiceTest extends UseCaseTest {

    @InjectMocks
    private UserConfirmationMigrationService service;

    @Mock
    private MongoTemplate cryptoMongoTemplate;

    @Mock
    private MongoTemplate adminMongoTemplate;

    @Test
    @DisplayName("Deve migrar documentos com sucesso quando existem dados")
    void deveMigrarDocumentosComSucesso() {
        Document doc1 = new Document("id", "1").append("email", "user1@test.com");
        Document doc2 = new Document("id", "2").append("email", "user2@test.com");
        Document doc3 = new Document("id", "3").append("email", "user3@test.com");

        List<Document> userConfirmations = Arrays.asList(doc1, doc2, doc3);

        when(cryptoMongoTemplate.findAll(Document.class, "user_confirmations"))
                .thenReturn(userConfirmations);

        UserConfirmationMigrationService testService = new UserConfirmationMigrationService(cryptoMongoTemplate, adminMongoTemplate);
        long result = testService.migrateUserConfirmations();

        assertEquals(3, result);
        verify(cryptoMongoTemplate, times(1)).findAll(Document.class, "user_confirmations");
        verify(adminMongoTemplate, times(1)).insert(any(List.class), anyString());
    }

    @Test
    @DisplayName("Deve retornar zero quando não existem documentos para migrar")
    void deveRetornarZeroQuandoNaoExistemDocumentos() {
        when(cryptoMongoTemplate.findAll(Document.class, "user_confirmations"))
                .thenReturn(Collections.emptyList());

        UserConfirmationMigrationService testService = new UserConfirmationMigrationService(cryptoMongoTemplate, adminMongoTemplate);
        long result = testService.migrateUserConfirmations();

        assertEquals(0, result);
        verify(cryptoMongoTemplate, times(1)).findAll(Document.class, "user_confirmations");
        verify(adminMongoTemplate, never()).insert(any(), anyString());
    }

    @Test
    @DisplayName("Deve migrar um único documento com sucesso")
    void deveMigrarUmUnicoDocumento() {
        Document doc = new Document("id", "1").append("email", "user@test.com");
        List<Document> userConfirmations = Collections.singletonList(doc);

        when(cryptoMongoTemplate.findAll(Document.class, "user_confirmations"))
                .thenReturn(userConfirmations);

        UserConfirmationMigrationService testService = new UserConfirmationMigrationService(cryptoMongoTemplate, adminMongoTemplate);
        long result = testService.migrateUserConfirmations();

        assertEquals(1, result);
        verify(cryptoMongoTemplate, times(1)).findAll(Document.class, "user_confirmations");
        verify(adminMongoTemplate, times(1)).insert(any(List.class), anyString());
    }

    @Test
    @DisplayName("Deve migrar documentos com estrutura complexa")
    void deveMigrarDocumentosComEstruturaComplexa() {
        Document doc1 = new Document("id", "1")
                .append("email", "user1@test.com")
                .append("confirmationCode", "ABC123")
                .append("createdAt", "2023-01-01T00:00:00Z")
                .append("metadata", new Document("source", "web").append("version", "1.0"));

        Document doc2 = new Document("id", "2")
                .append("email", "user2@test.com")
                .append("confirmationCode", "XYZ789")
                .append("createdAt", "2023-01-02T00:00:00Z")
                .append("metadata", new Document("source", "mobile").append("version", "2.0"));

        List<Document> userConfirmations = Arrays.asList(doc1, doc2);

        when(cryptoMongoTemplate.findAll(Document.class, "user_confirmations"))
                .thenReturn(userConfirmations);

        UserConfirmationMigrationService testService = new UserConfirmationMigrationService(cryptoMongoTemplate, adminMongoTemplate);
        long result = testService.migrateUserConfirmations();

        assertEquals(2, result);
        verify(cryptoMongoTemplate, times(1)).findAll(Document.class, "user_confirmations");
        verify(adminMongoTemplate, times(1)).insert(any(List.class), anyString());
    }

    @Test
    @DisplayName("Deve verificar se os templates corretos são utilizados")
    void deveVerificarTemplatesCorretos() {
        Document doc = new Document("id", "1").append("email", "user@test.com");
        List<Document> userConfirmations = Collections.singletonList(doc);

        when(cryptoMongoTemplate.findAll(Document.class, "user_confirmations"))
                .thenReturn(userConfirmations);

        UserConfirmationMigrationService testService = new UserConfirmationMigrationService(cryptoMongoTemplate, adminMongoTemplate);
        testService.migrateUserConfirmations();

        verify(cryptoMongoTemplate, times(1)).findAll(eq(Document.class), eq("user_confirmations"));
        verify(adminMongoTemplate, times(1)).insert(any(List.class), anyString());
        verifyNoMoreInteractions(cryptoMongoTemplate, adminMongoTemplate);
    }
}
