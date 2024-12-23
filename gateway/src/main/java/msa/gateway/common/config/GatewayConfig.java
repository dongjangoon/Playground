package msa.gateway.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import msa.gateway.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Getter
@RequiredArgsConstructor
@Configuration
public class GatewayConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    /**
     * API 라우팅
     * /api/**로 들어오는 요청을 8080포트 application 으로 전달.
     * /admin/** 의 경우 유저 권한 검사
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthorizationFilter jwtAuthorizationFilter) {
        return builder.routes()
                // /api/** 경로
                .route("api-route", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthorizationFilter) // JWT 필터
                                .rewritePath("/api/(?<segment>.*)", "/${segment}")) // Path Rewrite 필터
                        .uri("http://localhost:8080"))

                // /error-test 경로 (테스트용)
                .route("error-test-route", r -> r.path("/error-test")
                        .uri("http://localhost:8888")) // Gateway 모듈 내부에서 실행

                // /jwt-test 경로 (테스트용)
                .route("jwt-test-route", r -> r.path("/jwt-test")
                        .filters(f -> f.filter(jwtAuthorizationFilter)) // JWT 필터 적용
                        .uri("http://localhost:8888")) // Gateway 모듈 내부에서 실행
                        .build();
    }

}
