package msa.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

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
     * 테스트 2: Post 요청 확인
     */
    @Test
    void testCreateComment() {
        String requestBody = """
        {
          "postId": "12345",
          "content": "This is a sample comment",
          "authorId": "user-6789",
          "parentId": null
        }
        """;

        webTestClient.post()
                .uri("/api/comments") // 요청 경로
                .contentType(MediaType.APPLICATION_JSON) // 요청 Content-Type 설정
                .bodyValue(requestBody) // 요청 본문 설정
                .exchange() // 요청 실행
                .expectStatus().isOk() // HTTP 상태 200 확인
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8); // 응답 본문 디코딩
                    System.out.println("Full Response Body: " + responseBody); // 전체 응답 출력
                });
//                .jsonPath("$.id").exists() // ID가 반환되는지 확인
//                .jsonPath("$.postId").isEqualTo("12345")
//                .jsonPath("$.content").isEqualTo("This is a sample comment") // JSON 필드 검증
//                .jsonPath("$.authorId").isEqualTo("user-6789")
//                .jsonPath("$.parentId").isEmpty()
//                .jsonPath("$.createdAt").exists() // 생성 시간 확인
//                .jsonPath("$.updatedAt").exists(); // 업데이트 시간 확인

    }


}
