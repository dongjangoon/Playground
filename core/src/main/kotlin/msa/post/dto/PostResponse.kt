package msa.post.dto

import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.post.data.Post
import java.time.LocalDateTime

data class PostResponse(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val content: String,
    val summary: String,
    val category: PostCategory,
    val authorId: String,
    val tags: List<String>,
    val status: PostStatus,
    val viewCount: Long,
    val recommendCount: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun Post.toResponse() =
    PostResponse(
        id = id!!,
        title = title,
        sourceUrl = sourceUrl,
        content = content,
        summary = summary,
        category = category,
        authorId = authorId,
        tags = tags,
        status = status,
        viewCount = viewCount,
        recommendCount = recommendCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
