package msa.comment.repository

import msa.comment.data.Comment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CommentRepository : CoroutineCrudRepository<Comment, String> {
    suspend fun findByPostIdAndDeletedFalseOrderByCreatedAtDesc(postId: String): List<Comment>

    suspend fun findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId: String): List<Comment>

    suspend fun findByParentIdAndDeletedFalseOrderByCreatedAtDesc(parentId: String): List<Comment>
}
