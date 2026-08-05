package com.bookverse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test khởi chạy Spring Boot Application Context.
 */
@SpringBootTest
@ActiveProfiles("dev")
class BookVerseApplicationTests {

    @Test
    void contextLoads() {
        // Kiểm tra application context load thành công
    }
}
