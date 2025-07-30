package com.organlink;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test for OrganLink application
 */
@SpringBootTest
@ActiveProfiles("test")
class OrganLinkApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // It will fail if there are any configuration issues or missing dependencies
    }
}
