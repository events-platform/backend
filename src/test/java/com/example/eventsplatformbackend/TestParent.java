package com.example.eventsplatformbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class TestParent {
    @Value("${test.username}")
    protected String username;
    @Value("${test.email}")
    protected String email;
    @Value("${test.password}")
    protected String password;
}
