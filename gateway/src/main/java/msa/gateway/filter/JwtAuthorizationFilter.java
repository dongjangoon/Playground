package msa.gateway.filter;

import lombok.AllArgsConstructor;
import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import msa.gateway.common.jwt.JwtTokenProvider;
import msa.gateway.common.jwt.JwtTokenValidator;
import msa.gateway.common.jwt.JwtUtil;
import msa.gateway.common.jwt.JwtExchangeGenerator;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;

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

                // 3. 유저 정보 확인 및 요청 헤더 생성
                .flatMap(claims -> JwtExchangeGenerator.GENERATOR.apply(claims, exchange))

                .flatMap(chain::filter)
                .onErrorResume(e -> Mono.error(new CustomException(ErrorType.UNAUTHORIZED)));
    }
}
