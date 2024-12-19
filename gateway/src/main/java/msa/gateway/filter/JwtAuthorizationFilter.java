package msa.gateway.filter;

import lombok.AllArgsConstructor;
import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import msa.gateway.common.jwt.JwtTokenProvider;
import msa.gateway.common.jwt.JwtTokenValidator;
import msa.gateway.common.jwt.JwtUtil;
import msa.gateway.common.jwt.JwtUserInfoExtractor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthorizationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.just(exchange)
                // 1. 헤더에서 토큰 추출
                .map(ex -> JwtTokenProvider.TOKEN_EXTRACTOR.apply(ex.getRequest().getHeaders()))

                // 2. JWT 검증 및 Claims 추출
                .map(token -> JwtTokenValidator.VALIDATE_TOKEN.apply(token, jwtUtil.getSecretKey()))

                // 3. 유저 정보 추출 (추후에 유저정보를 사용하기 위함)
                .flatMap(JwtUserInfoExtractor.EXTRACTOR)

                // 현재는 토큰 유효성 검사만 이루어지며 실제 필터링 로직은 이곳에 위치할 예정 (ex. ADMIN 검증 등)

                .flatMap(claims -> chain.filter(exchange))
                .onErrorResume(e -> Mono.error(new CustomException(ErrorType.UNAUTHORIZED)));
    }
}
