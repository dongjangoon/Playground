package msa.gateway.common.jwt;

import io.jsonwebtoken.Claims;
import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public class JwtExchangeGenerator {

    // 함수형 처리기
    public static final BiFunction<Claims, ServerWebExchange, Mono<ServerWebExchange>> GENERATOR = (claims, exchange) -> {
        String userEmail = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        // ADMIN 경로에 대한 권한 확인
        if (exchange.getRequest().getPath().toString().startsWith("/admin") && !"ADMIN".equals(role)) {
            return Mono.error(new CustomException(ErrorType.FORBIDDEN));
        }

        // 새로운 요청 헤더 생성
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Email", userEmail)
                        .header("X-User-Role", role)
                        .build())
                .build();

        return Mono.just(mutatedExchange);
    };
}
