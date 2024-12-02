package msa.gateway.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GatewayConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfigTest.class);

    @Autowired
    private GatewayConfig gatewayConfig;

    @Test
    void testEntrypointApiPath() {
        logger.info("Testing entrypointApiPath...");
        String path = gatewayConfig.getEntrypointApiPath();
        logger.debug("Entrypoint API Path: {}", path);

        assertThat(path).isNotNull().isEqualTo("http://localhost:8888");
        logger.info("Entrypoint API Path test passed!");
    }

    @Test
    void testCustomRouteLocator() {
        logger.info("Testing customRouteLocator...");
        var routeLocator = gatewayConfig.customRouteLocator(null); // RouteLocatorBuilder Mock이 없으므로 null 사용
        assertThat(routeLocator).isNotNull();
        logger.info("RouteLocator successfully created and test passed!");
    }
}
