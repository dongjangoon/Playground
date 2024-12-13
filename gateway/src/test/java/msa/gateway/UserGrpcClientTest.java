package msa.gateway;

import msa.gateway.client.UserGrpcClient;
import msa.user.service.UserOuterClass.GetUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserGrpcClientTest {

    private UserGrpcClient client;

    @BeforeEach
    void setUp() {
        // UserGrpcClient 초기화
        client = new UserGrpcClient();
    }

    @Test
    void testGetUser() {
        // Given
        Long testUserId = 123L;

        // When
        GetUserResponse response = client.getUser(testUserId);

        // Then
        assertEquals(123L, response.getId());
        assertEquals("mockuser@example.com", response.getEmail());
        assertEquals("MockUser", response.getNickname());
        assertEquals("USER", response.getRole().name());
        assertEquals("ACTIVE", response.getStatus().name());
        System.out.println("Test passed: " + response);
    }
}
