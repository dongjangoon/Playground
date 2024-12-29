package msa.handler

import msa.common.enum.PostCategory
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
        val createPostRequest = request.awaitBody<CreatePostRequest>()
        val post = postService.createPost(createPostRequest)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun updatePost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val updatePostRequest = request.awaitBody<UpdatePostRequest>()
        val post = postService.updatePost(id, updatePostRequest)
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

    /**
     * 사용자의 IP 주소와 User-Agent를 조합하여 고유 식별자를 생성합니다.
     */
    private fun createIdentifier(request: ServerRequest): String {
        val ip = request.remoteAddress().get().address.hostAddress
        val userAgent = request.headers().firstHeader("User-Agent") ?: "unknown"
        return "$ip:$userAgent".hashCode().toString()
    }

    suspend fun getPost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val identifier = createIdentifier(request)
        val post = postService.getPostAndIncrementViewCount(id, identifier)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    suspend fun recommendPost(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val identifier = createIdentifier(request)
        val post = postService.recommend(id, identifier)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(post.toResponse())
    }

    /**
     * 커서 기반 페이징을 사용하여 게시글 목록을 조회합니다.
     */
    suspend fun getPostsWithCursor(request: ServerRequest): ServerResponse {
        val cursor = request.queryParamOrNull("cursor")
        val category =
            request.queryParamOrNull("category")?.let {
                PostCategory.valueOf(it.uppercase())
            }
        val sortBy = request.queryParamOrNull("sortBy")
        val limit = request.queryParamOrNull("limit")?.toInt() ?: 20

        val page = postService.getPostsWithCursor(cursor, category, sortBy, limit)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(page)
    }
}
