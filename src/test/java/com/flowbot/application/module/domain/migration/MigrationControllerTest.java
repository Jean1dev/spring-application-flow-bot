package com.flowbot.application.module.domain.migration;

import com.flowbot.application.E2ETests;
import com.flowbot.application.module.domain.migration.service.UserConfirmationMigrationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MigrationControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_VERSION));

    @MockBean
    private UserConfirmationMigrationService userConfirmationMigrationService;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.principal.uri", MONGO_CONTAINER::getReplicaSetUrl);
        registry.add("mongodb.admin.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    void shouldMigrateUserConfirmationsSuccessfully() throws Exception {
        assertTrue(MONGO_CONTAINER.isRunning());
        when(userConfirmationMigrationService.migrateUserConfirmations()).thenReturn(5L);

        mvc.perform(post("/migration/user-confirmations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.migratedDocuments").value(5))
                .andExpect(jsonPath("$.message").value("Migration completed successfully"));
    }

    @Test
    void shouldReturnZeroWhenNoDocumentsToMigrate() throws Exception {
        assertTrue(MONGO_CONTAINER.isRunning());
        when(userConfirmationMigrationService.migrateUserConfirmations()).thenReturn(0L);

        mvc.perform(post("/migration/user-confirmations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.migratedDocuments").value(0))
                .andExpect(jsonPath("$.message").value("Migration completed successfully"));
    }
}
