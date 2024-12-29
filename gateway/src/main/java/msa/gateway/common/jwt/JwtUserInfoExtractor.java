package msa.gateway.common.jwt;

import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public class JwtUserInfoExtractor {

    // JWT에서 유저 정보 추출 Only
    public static final Function<Claims, Mono<Claims>> EXTRACTOR = (claims) -> {
        // 유저 이메일과 역할 추출
        String userEmail = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        log.info("Extracted User Info - Email: {}, Role: {}", userEmail, role);

        return Mono.just(claims);
    };
}
