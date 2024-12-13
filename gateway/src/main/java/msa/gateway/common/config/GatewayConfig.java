package msa.gateway.common.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import msa.gateway.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Slf4j
@Configuration
public class GatewayConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public GatewayConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Value("${user.service.uri}")
    private String userServiceUri;

    @Value("${api.service.uri}")
    private String apiServiceUri;

    @Value("${user.service.base-path}")
    private String userServiceBasePath;

    /**
     * API 라우팅 설정
     * - /api/** 요청: JWT 필터 적용 후 API 서비스로 전달
     * - /admin/** 요청: JWT 필터 적용 후 API 서비스로 전달
     * - /login 요청: JWT 필터 우회 후 User 서비스로 전달
     * - /signup 요청: JWT 필터 우회 후 User 서비스로 전달
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // /login 요청: User 서비스의 /api/v1/users/login으로 라우팅
                .route("login-route", r -> r.path("/login")
                        .filters(f -> f.rewritePath("/login", userServiceBasePath + "/login"))
                        .uri(userServiceUri))

                // /signup 요청: User 서비스의 /api/v1/users/signup으로 라우팅
                .route("signup-route", r -> r.path("/signup")
                        .filters(f -> f.rewritePath("/signup", userServiceBasePath + "/signup"))
                        .uri(userServiceUri))

                // /api/** 요청: JWT 필터 적용 후 API 서비스로 전달
                .route("api-route", r -> r.path("/api/**")
                        .filters(f -> f.filter(jwtAuthorizationFilter)
                                .rewritePath("/api/(?<segment>.*)", "/${segment}"))
                        .uri(apiServiceUri))

                // /admin/** 요청: JWT 필터 적용 후 API 서비스로 전달
                .route("admin-route", r -> r.path("/admin/**")
                        .filters(f -> f.filter(jwtAuthorizationFilter)) // JWT 필터만 추가
                        .uri(apiServiceUri))
                // /error-test 경로 (테스트용)
                .route("error-test-route", r -> r.path("/error-test")
                        .uri("http://localhost:8888")) // Gateway 모듈 내부에서 실행
                .build();
    }
}
