package com.jh.chat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "security.jwt.secret=0123456789abcdef0123456789abcdef")
class NettyChatApplicationTests {

    @Test
    void contextLoads() {
    }

}
