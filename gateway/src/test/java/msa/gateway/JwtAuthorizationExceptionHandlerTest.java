package msa.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import msa.gateway.common.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtAuthorizationExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void 토큰_추출되서_Claim_획득() {
        // JWT 토큰 생성
        String jwtToken = Jwts.builder()
                .setSubject("test-user")
                .claim("email", "test@example.com")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS256, jwtUtil.getSecretKey().getBytes())
                .compact();

        System.out.println("Generated JWT Token: " + jwtToken);
        System.out.println("JWT Token Length: " + jwtToken.length());

        // TODO: 로그보니까 똑바로 나오는것으로 보아 로그검증하는 쪽으로 방향 틀어서 테스트해야함
        // WebTestClient로 요청 및 응답 로그 출력
        webTestClient.get()
                .uri("/jwt-test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectBody()
                .consumeWith(entityExchangeResult -> {
                    // 요청 헤더 출력
                    HttpHeaders requestHeaders = entityExchangeResult.getRequestHeaders();
                    System.out.println("Request Headers: " + requestHeaders);

                    // 응답 본문 확인
                    String responseBody = new String(entityExchangeResult.getResponseBody());
                    System.out.println("Response Body: " + responseBody);

                    // 검증
                    assert responseBody.contains("JWT 토큰이 유효합니다.");
                });
    }
}
