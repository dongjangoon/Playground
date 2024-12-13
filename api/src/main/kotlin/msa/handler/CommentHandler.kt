package msa.handler

import msa.comment.dto.CreateCommentRequest
import msa.comment.dto.toResponse
import msa.comment.service.CommentService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class CommentHandler(
    private val commentService: CommentService,
) {
    suspend fun createComment(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<CreateCommentRequest>()
        val comment =
            commentService.createComment(
                postId = body.postId,
                content = body.content,
                authorId = body.authorId,
                parentId = body.parentId,
            )
        return ServerResponse.ok()
            .bodyValueAndAwait(comment.toResponse())
    }

    suspend fun updateComment(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val body = request.awaitBody<CreateCommentRequest>()
        val comment =
            commentService.updateComment(
                id = id,
                content = body.content,
            )
        return ServerResponse.ok()
            .bodyValueAndAwait(comment.toResponse())
    }

    suspend fun deleteComment(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val comment = commentService.deleteComment(id)
        return ServerResponse.ok()
            .bodyValueAndAwait(comment.toResponse())
    }

    suspend fun getComment(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val comment = commentService.getComment(id)
        return ServerResponse.ok()
            .bodyValueAndAwait(comment.toResponse())
    }

    suspend fun getCommentsByPost(request: ServerRequest): ServerResponse {
        val postId = request.pathVariable("postId")
        val comments = commentService.getCommentsByPost(postId)
        return ServerResponse.ok()
            .bodyValueAndAwait(comments.map { it.toResponse() })
    }

    suspend fun getCommentsByAuthor(request: ServerRequest): ServerResponse {
        val authorId = request.pathVariable("authorId")
        val comments = commentService.getCommentsByAuthor(authorId)
        return ServerResponse.ok()
            .bodyValueAndAwait(comments.map { it.toResponse() })
    }

    suspend fun getReplies(request: ServerRequest): ServerResponse {
        val parentId = request.pathVariable("parentId")
        val comments = commentService.getReplies(parentId)
        return ServerResponse.ok()
            .bodyValueAndAwait(comments.map { it.toResponse() })
    }
}
