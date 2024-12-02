package msa.gateway.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class GatewayConfig {

    @Value("${spring.entrypoint.api.path}")
    private String entrypointApiPath;

    // 라우팅 설정 추가
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("entrypoint-route", r -> r.path("/api/**").uri(entrypointApiPath))
                .build();
    }
}
