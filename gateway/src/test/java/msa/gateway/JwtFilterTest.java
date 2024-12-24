package msa.gateway;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import msa.gateway.common.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtFilterTest {

    private ListAppender<ILoggingEvent> logCaptor;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    @InjectMocks
    private msa.gateway.common.jwt.JwtUserInfoExtractor jwtUserInfoExtractor;

    @BeforeEach
    public void setUp() {
        // Logback ListAppender 설정
        Logger logger = (Logger) LoggerFactory.getLogger(msa.gateway.common.jwt.JwtUserInfoExtractor.class);
        logCaptor = new ListAppender<>();
        logCaptor.start();
        logger.addAppender(logCaptor);
    }

    private String generateTestJwt(String email, String role) {
        return Jwts.builder()
                .setSubject("test-user")
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS256, jwtUtil.getSecretKey().getBytes())
                .compact();
    }

    @Test
    public void Claim_에서_유저_정보_추출_로그_검증() {
        // 1. 테스트용 JWT 생성
        String email = "test@example.com";
        String role = "USER";
        String jwt = generateTestJwt(email, role);

        // 2. JWT로 API 호출 (응답 검증 생략)
        webTestClient.get()
                .uri("/jwt-test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .exchange();

        // 3. 로그 검증
        String capturedLogs = logCaptor.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.joining("\n"));

        System.out.printf("Captured Logs:\n%s\n", capturedLogs);

        assertTrue(capturedLogs.contains("Extracted User Info - Email: test@example.com, Role: USER"));
    }
}
