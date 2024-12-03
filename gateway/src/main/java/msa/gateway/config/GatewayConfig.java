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

    @Value("${test.api.path}")
    private String apiPath;

//    @Value("${spring.another.service.uri}")
//    private String anotherServiceUri;

    /**
     * 기본 API 경로 라우팅
     * /api/**로 들어오는 요청을 entrypointApiPath로 전달.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("api-route", r -> r.path("/health-check").uri(apiPath
                ))
                .build();
    }

    /**
     * 경로 필터 추가
     * /service/** 경로를 처리하며, 경로를 변경한 후 대상 서비스로 요청 전달.
     */
//    @Bean
//    public RouteLocator rewritePathRoute(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("rewrite-path-route", r -> r.path("/service/**")
//                        .filters(f -> f.rewritePath("/service/(?<segment>.*)", "/${segment}"))
//                        .uri(anotherServiceUri))
//                .build();
//    }

    /**
     * 요청 헤더 추가
     * 특정 경로로 들어오는 요청에 Authorization 헤더를 추가.
     */
//    @Bean
//    public RouteLocator addHeaderRoute(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("add-header-route", r -> r.path("/secure/**")
//                        .filters(f -> f.addRequestHeader("Authorization", "Bearer my_secure_token"))
//                        .uri(anotherServiceUri))
//                .build();
//    }

}
