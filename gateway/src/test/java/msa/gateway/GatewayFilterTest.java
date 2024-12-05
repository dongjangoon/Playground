package msa.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import msa.gateway.common.config.GatewayConfig;
import msa.gateway.common.utils.JwtUtil;
import msa.gateway.filter.JwtAuthorizationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // WebFluxTest 에서는 에러 발생
@Import({GatewayConfig.class, JwtAuthorizationFilter.class, JwtUtil.class}) // 테스트 대상 구성 및 필터
public class GatewayFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    // 빈 주입에서 에러가 발생하는 코드
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    private final SecretKey secretKey = Keys.hmacShaKeyFor(jwtUtil.getSecretKey().getBytes(StandardCharsets.UTF_8));

    // 정상적으로 동작하는 코드
    @Autowired
    private JwtUtil jwtUtil;

    private SecretKey secretKey;

    @BeforeEach
    void setup() {
        // jwtUtil 주입 이후 secretKey 초기화
        secretKey = Keys.hmacShaKeyFor(jwtUtil.getSecretKey().getBytes(StandardCharsets.UTF_8));
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
