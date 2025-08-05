package com.smartcare;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.data.seed=false")
@DisplayName("Smart Care Services Application Tests")
class SmartcareServicesApplicationTests {

    @Test
    @DisplayName("Should load application context")
    void contextLoads() {
        // This test will fail if the application context cannot start
        // It's a basic smoke test to ensure all beans can be created
    }

}
