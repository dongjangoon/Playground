package msa.router

import msa.handler.NicknameHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Component
class MainRouter(
    private val nicknameHandler: NicknameHandler,
) {
    @Bean
    fun healthCheck() =
        coRouter {
            GET("/health-check") { ServerResponse.ok().buildAndAwait() }
        }

    @Bean
    fun nicknameRoute() =
        v1CoRouter {
            "/nicknames".nest {
                GET("", nicknameHandler::generateNickname)
                POST("/custom", nicknameHandler::generateCustomNickname)
                PATCH("/{id}/use", nicknameHandler::markAsUsed)
                GET("/unused", nicknameHandler::getUnusedNickname)
            }
        }

    private fun v1CoRouter(r: CoRouterFunctionDsl.() -> Unit) =
        coRouter {
            path("/v1/api").or("").nest(r)
        }
}
