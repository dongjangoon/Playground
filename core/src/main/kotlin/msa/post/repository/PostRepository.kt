package msa.post.repository

import kotlinx.coroutines.flow.Flow
import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.post.data.Post
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface PostRepository : CoroutineCrudRepository<Post, String> {
    suspend fun findByAuthorId(authorId: String): List<Post>

    suspend fun findByCategory(category: PostCategory): List<Post>

    suspend fun findByStatus(status: PostStatus): List<Post>

    // 최신순 정렬
    fun findByCreatedAtLessThanOrderByCreatedAtDesc(
        createdAt: LocalDateTime,
        limit: Int,
    ): Flow<Post>

    // 추천순 정렬
    fun findByRecommendCountLessThanAndCreatedAtLessThanOrderByRecommendCountDescCreatedAtDesc(
        recommendCount: Long,
        createdAt: LocalDateTime,
        limit: Int,
    ): Flow<Post>

    fun findByCategoryAndCreatedAtLessThanOrderByCreatedAtDesc(
        category: PostCategory,
        createdAt: LocalDateTime,
        limit: Int,
    ): Flow<Post>
}
