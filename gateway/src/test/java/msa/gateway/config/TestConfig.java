package msa.gateway.config;

import msa.gateway.common.error.GlobalExceptionHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestConfiguration
public class TestConfig {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
        return new GlobalExceptionHandler(objectMapper);
    }

    @Bean
    public WebTestClient webTestClient() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:8888") // Gateway의 주소
                .build();
    }
}
