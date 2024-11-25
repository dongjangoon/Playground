package msa.comment.dto

import msa.comment.data.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: String,
    val postId: String,
    val content: String,
    val authorId: String,
    val parentId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun Comment.toResponse() = CommentResponse(
    id = id!!,
    postId = postId,
    content = content,
    authorId = authorId,
    parentId = parentId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
