package msa.handler

import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.post.dto.CreatePostRequest
import msa.post.dto.UpdatePostRequest
import msa.post.dto.toResponse
import msa.post.service.PostService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class PostHandler(
    private val postService: PostService,
) {
    suspend fun createPost(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreatePostRequest>()
        val post = postService.createPost(
            body.title,
            body.content,
            body.summary,
            body.category,
            body.authorId,
            body.tags
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun updatePost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val body = request.awaitBody<UpdatePostRequest>()
        val post = postService.updatePost(
            id,
            body.title,
            body.content,
            body.summary,
            body.category,
            body.tags
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun publishPost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val post = postService.publishPost(id)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun getPost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val post = postService.getPost(id)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun getPosts(request: ServerRequest): ServerResponse {
        val category = request.queryParamOrNull("category")?.let {
            PostCategory.valueOf(it.uppercase())
        }
        val status = request.queryParamOrNull("status")?.let {
            PostStatus.valueOf(it.uppercase())
        }

        val posts = postService.getPosts(category, status)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(posts.map { it.toResponse() })
    }

    suspend fun getPostsByAuthor(request: ServerRequest): ServerResponse {
        val authorId = request.pathVariable("authorId")
        val posts = postService.getPostsByAuthorId(authorId)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(posts.map { it.toResponse() })
    }
}
