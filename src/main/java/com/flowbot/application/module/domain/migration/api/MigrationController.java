package com.flowbot.application.module.domain.migration.api;

import com.flowbot.application.module.domain.migration.service.UserConfirmationMigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/migration")
public class MigrationController {

    private final UserConfirmationMigrationService userConfirmationMigrationService;

    public MigrationController(UserConfirmationMigrationService userConfirmationMigrationService) {
        this.userConfirmationMigrationService = userConfirmationMigrationService;
    }

    @PostMapping("/user-confirmations")
    public ResponseEntity<Map<String, Object>> migrateUserConfirmations() {
        long migratedCount = userConfirmationMigrationService.migrateUserConfirmations();

        Map<String, Object> response = Map.of(
                "migratedDocuments", migratedCount,
                "message", "Migration completed successfully"
        );

        return ResponseEntity.ok(response);
    }
}
