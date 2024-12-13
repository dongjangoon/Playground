package msa.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtAuthorizationExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testMissingAuthorizationHeader_shouldReturnUnauthorized() {
        webTestClient.get()
                .uri("/api/resource") // JWT 필터가 적용된 경로
                .exchange()
                .expectStatus().isUnauthorized() // 401 Unauthorized
                .expectBody()
                .jsonPath("$.message").isEqualTo("Missing or invalid Authorization header")
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.code").isEqualTo("C401");
    }
}
