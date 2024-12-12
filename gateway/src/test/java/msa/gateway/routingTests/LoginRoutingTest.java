package msa.gateway.routingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 랜덤 포트로 실행
public class LoginRoutingTest {

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        // 요청 보낼 대상 URL과 포트 설정 (http://localhost:8888)
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:8888").build();
        log.info("WebTestClient initialized with base URL: http://localhost:8888");
    }

    /**
     * 회원가입과 로그인 요청 테스트
     * - 회원가입: /signup 경로에 POST 요청을 보내 사용자 정보를 전달합니다.
     * - 로그인: /login 경로에 POST 요청을 보내 인증 정보를 전달합니다.
     */
    @Test
    public void testSignupAndLoginRoute() {

        log.info("WebTestClient base URI: {}", webTestClient);

        // 회원가입 요청에 사용할 Mock JSON 데이터
        String signupRequestJson = "{\n" +
                "  \"email\": \"testuser2@example.com\",\n" +
                "  \"password\": \"Test@1234\",\n" +
                "  \"nickname\": \"testuser2\"\n" +
                "}";

        log.info("Sending signup request to /signup with payload: {}", signupRequestJson);

        // /signup 경로에 POST 요청 수행
        webTestClient.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signupRequestJson)
                .exchange()
                .expectStatus().isCreated() // 상태 코드 201 Created 기대
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

        // 로그인 요청에 사용할 Mock JSON 데이터
        String loginRequestJson = "{\n" +
                "  \"email\": \"testuser2@example.com\",\n" +
                "  \"password\": \"Test@1234\"\n" +
                "}";

        log.info("Sending login request to /login with payload: {}", loginRequestJson);

        // /login 경로에 POST 요청 수행 및 응답 검증
        webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().isOk() // 상태 코드 200 OK 기대
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.accessToken").isNotEmpty() // accessToken 필드가 존재하고 비어있지 않음
                .jsonPath("$.data.refreshToken").isNotEmpty() // refreshToken 필드가 존재하고 비어있지 않음
                .jsonPath("$.data.tokenType").isEqualTo("Bearer") // tokenType이 Bearer인지 확인
                .jsonPath("$.data.accessTokenExpiresIn").isNumber() // accessToken 만료 시간이 숫자인지 확인
                .jsonPath("$.data.refreshTokenExpiresIn").isNumber(); // refreshToken 만료 시간이 숫자인지 확인

        log.info("Login request completed successfully with valid tokens.");
    }
}
