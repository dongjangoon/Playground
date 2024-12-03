package msa.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * 테스트 1: /api/** 요청이 올바른 경로로 라우팅되는지 확인
     */
    @Test
    void testApiRoute() {
        webTestClient.get()
                .uri("/api/health-check") // Gateway로 요청을 보냄
                .exchange() // 요청 실행
                .expectStatus().isOk(); // HTTP 상태 코드 200 확인
    }

    /**
     * 테스트 2: 필터가 동작하여 경로가 변경되는지 확인
     */
    /*
    @Test
    void testPathRewrite() {
        webTestClient.get()
                .uri("/service/some-path") // Gateway에 요청
                .exchange() // 요청 실행
                .expectStatus().isOk() // HTTP 상태 코드 200 확인
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    System.out.println("Response: " + body);
                    // 대상 서비스에서 경로가 변경된 후의 응답 확인
                    assert body != null && body.contains("Rewritten Path Successful");
                });
    }
    */

    /**
     * 테스트 3: 요청 헤더가 추가되었는지 확인
     */
    /*
    @Test
    void testRequestHeaderAddition() {
        webTestClient.get()
                .uri("/secure/test") // Gateway로 요청
                .exchange() // 요청 실행
                .expectStatus().isOk() // HTTP 상태 코드 200 확인
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    System.out.println("Response: " + body);
                    // 대상 서비스가 추가된 헤더를 인식했는지 확인
                    assert body != null && body.contains("Authorization Header Processed");
                });
    }
    */
}
