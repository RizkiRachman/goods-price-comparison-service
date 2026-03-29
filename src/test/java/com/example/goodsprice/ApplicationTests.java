package com.example.goodsprice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Integration tests for {@link Application}.
 *
 * <p>Verifies that the Spring Boot application context loads successfully
 * with all required configurations and beans.</p>
 *
 * @author Dev Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Tests")
class ApplicationTests {

    @Test
    @DisplayName("Should load application context successfully")
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // If context loads, test passes
        }, "Application context should load without errors");
    }

}