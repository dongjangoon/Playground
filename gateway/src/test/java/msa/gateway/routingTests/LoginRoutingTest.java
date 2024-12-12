package msa.gateway.routingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@WebFluxTest
public class LoginRoutingTest {

    @Autowired
    private WebTestClient webTestClient;

    @Value("${user.service.uri}")
    private String userServiceUri;

    @Test
    public void testSignupAndLoginRoute() {
        // Mock SignupRequest JSON payload
        String signupRequestJson = "{\n" +
                "  \"email\": \"testuser@example.com\",\n" +
                "  \"password\": \"Test@1234\",\n" +
                "  \"nickname\": \"testuser\"\n" +
                "}";

        // Perform POST request to /signup
        webTestClient.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signupRequestJson)
                .exchange()
                .expectStatus().isCreated() // Expect HTTP 201 Created
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

        // Mock LoginRequest JSON payload
        String loginRequestJson = "{\n" +
                "  \"email\": \"testuser@example.com\",\n" +
                "  \"password\": \"Test@1234\"\n" +
                "}";

        // Perform POST request to /login
        webTestClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequestJson)
                .exchange()
                .expectStatus().isOk() // Expect HTTP 200 OK
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.token").isNotEmpty(); // Ensure the token field is present and not empty
    }
}
