package msa.gateway.config;

import lombok.Getter;
import msa.gateway.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class GatewayConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public GatewayConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Value("${test.api.path}")
    private String apiPath;

    /**
     * API 라우팅
     * /api/**로 들어오는 요청을 8080포트 application 으로 전달.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("api-route", r -> r.path("/api/**")
                        .filters(f -> f.rewritePath("/api/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8080"))
                .build();
    }
}
