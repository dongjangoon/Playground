package msa.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthorizationFilter implements GatewayFilter {

    private static final String SECRET_KEY = "DG3K2NG9lK3T2FLfnO283HO1NFLAy9FGJ23UM9Rv923YRV923HT";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        // JWT가 없는 경우 요청 차단
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        try {
            // JWT 검증
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 인가 로직: 특정 역할만 허용
            String role = claims.get("role", String.class);
            if (!"ADMIN".equals(role)) {
                return onError(exchange, "Forbidden: You do not have the required role", HttpStatus.FORBIDDEN);
            }

            // 요청을 다음 체인으로 전달
            return chain.filter(exchange);

        } catch (Exception e) {
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    // 에러 응답 처리
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(err.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
