package msa.content.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "crawled_urls")
data class CrawledUrl(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val url: String,
    /** 크롤링으로 생성한 Post ID 참조 */
    val postId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            url: String,
            postId: String,
        ) = CrawledUrl(
            url = url,
            postId = postId,
        )
    }
}
