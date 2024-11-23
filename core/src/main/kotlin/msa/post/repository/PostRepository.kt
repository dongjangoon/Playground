package msa.post.repository

import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.post.data.Post
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, String> {
    suspend fun findByAuthorId(authorId: String): List<Post>
    suspend fun findByCategory(category: PostCategory): List<Post>
    suspend fun findByStatus(status: PostStatus): List<Post>
    suspend fun findByCategoryAndStatus(category: PostCategory, status: PostStatus): List<Post>
}
