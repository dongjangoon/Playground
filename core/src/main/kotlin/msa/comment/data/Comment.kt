package msa.comment.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "comments")
data class Comment(
    @Id
    val id: String? = null,

    @Indexed
    val postId: String,

    @Indexed
    val authorId: String,

    val parentId: String? = null,   // 대댓글을 위한 부모 댓글 ID
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false,   // Soft delete
) {
    companion object {
        fun create(
            content: String,
            postId: String,
            authorId: String,
            parentId: String? = null,
        ) = Comment(
            content = content,
            postId = postId,
            authorId = authorId,
            parentId = parentId,
        )
    }
}
