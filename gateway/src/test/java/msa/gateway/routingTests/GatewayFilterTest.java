package msa.gateway.routingTests;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import msa.gateway.common.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    private SecretKey secretKey; // final 제거

    @PostConstruct
    public void init() {
        // 의존성 주입 이후에 secretKey 초기화
        this.secretKey = Keys.hmacShaKeyFor(jwtUtil.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    private String generateJwtToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10분 유효
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    public void testAdminRoute_withValidAdminToken_shouldPass() {
        // 유효한 ADMIN JWT 생성
        String token = generateJwtToken("admin@example.com", "ADMIN");

        // 요청 실행 및 검증
        webTestClient.get()
                .uri("/admin/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk() // 200 OK
                .expectHeader().exists("X-User-Email")
                .expectHeader().valueEquals("X-User-Role", "ADMIN");
    }

    @Test
    public void testAdminRoute_withNonAdminToken_shouldFail() {
        // 유효한 NON-ADMIN JWT 생성
        String token = generateJwtToken("user@example.com", "USER");

        // 요청 실행 및 검증
        webTestClient.get()
                .uri("/admin/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden() // 403 Forbidden
                .expectBody(String.class).isEqualTo("Forbidden: Admin access only");
    }

    @Test
    public void testApiRoute_withValidUserToken_shouldPass() {
        // 유효한 USER JWT 생성
        String token = generateJwtToken("user@example.com", "USER");

        // 요청 실행 및 검증
        webTestClient.get()
                .uri("/api/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk() // 200 OK
                .expectHeader().exists("X-User-Email")
                .expectHeader().valueEquals("X-User-Role", "USER");
    }

    @Test
    public void testApiRoute_withInvalidToken_shouldFail() {
        // 잘못된 JWT
        String token = "invalid.jwt.token";

        // 요청 실행 및 검증
        webTestClient.get()
                .uri("/api/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isUnauthorized() // 401 Unauthorized
                .expectBody(String.class).isEqualTo("Invalid or expired token");
    }
}
