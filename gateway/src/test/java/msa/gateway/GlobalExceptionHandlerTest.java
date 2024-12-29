package msa.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testBusinessExceptionHandling() {
        webTestClient.get()
                .uri("/error-test")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("프레임워크 내부 오류가 발생하였습니다.")
                .jsonPath("$.status").isEqualTo("INTERNAL_SERVER_ERROR"); // 기대값을 String으로 변경
    }
}
