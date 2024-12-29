package msa.post.data

import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "posts")
data class Post(
    @Id
    val id: String? = null,
    @Indexed
    val title: String,
    val sourceUrl: String,
    val content: String,
    val summary: String,
    val category: PostCategory,
    val authorId: String,
    val tags: List<String>,
    val status: PostStatus,
    val viewCount: Long = 0,
    val recommendCount: Long = 0,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun create(
            title: String,
            sourceUrl: String,
            content: String,
            summary: String,
            category: PostCategory,
            authorId: String,
            tags: List<String>,
        ) = Post(
            title = title,
            sourceUrl = sourceUrl,
            content = content,
            summary = summary,
            category = category,
            authorId = authorId,
            tags = tags,
            status = PostStatus.DRAFT,
            viewCount = 0,
            recommendCount = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }
}
