package msa.gateway.common.jwt;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class JwtUserInfoExtractor {

    // JWT에서 유저 정보 추출 Only
    public static final Function<Claims, Mono<Claims>> EXTRACTOR = (claims) -> {
        // 유저 이메일과 역할 추출
        String userEmail = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return Mono.just(claims);
    };
}
