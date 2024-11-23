package msa.router

import msa.handler.NicknameHandler
import msa.handler.PostHandler
import msa.router.docs.PostDocs
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Component
class MainRouter(
    private val nicknameHandler: NicknameHandler,
    private val postHandler: PostHandler,
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

    @Bean
    @PostDocs
    fun postRoute() =
        v1CoRouter {
            "/posts".nest {
                POST("", postHandler::createPost)
                PUT("/{id}", postHandler::updatePost)
                PATCH("/{id}/publish", postHandler::publishPost)
                GET("/{id}", postHandler::getPost)
                GET("", postHandler::getPosts)
                GET("/author/{authorId}", postHandler::getPostsByAuthor)
            }
        }

    private fun v1CoRouter(r: CoRouterFunctionDsl.() -> Unit) =
        coRouter {
            path("/v1/api").or("").nest(r)
        }
}
