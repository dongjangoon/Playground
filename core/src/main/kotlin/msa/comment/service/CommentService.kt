package msa.comment.service

import msa.comment.data.Comment
import msa.comment.repository.CommentRepository
import msa.common.exception.CommentNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface CommentService {
    suspend fun createComment(
        postId: String,
        content: String,
        authorId: String,
        parentId: String? = null,
    ): Comment

    suspend fun updateComment(
        id: String,
        content: String,
    ): Comment

    suspend fun deleteComment(id: String): Comment

    suspend fun getComment(id: String): Comment

    suspend fun getCommentsByPost(postId: String): List<Comment>

    suspend fun getCommentsByAuthor(authorId: String): List<Comment>

    suspend fun getReplies(parentId: String): List<Comment>
}

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
) : CommentService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun createComment(
        postId: String,
        content: String,
        authorId: String,
        parentId: String?,
    ): Comment =
        Comment.create(postId, content, authorId, parentId)
            .let { commentRepository.save(it) }

    override suspend fun updateComment(
        id: String,
        content: String,
    ): Comment =
        commentRepository.findById(id)?.let { comment ->
            commentRepository.save(
                comment.copy(
                    content = content,
                    updatedAt = LocalDateTime.now(),
                ),
            )
        } ?: throw CommentNotFoundException

    override suspend fun deleteComment(id: String): Comment =
        commentRepository.findById(id)?.let { comment ->
            commentRepository.save(
                comment.copy(
                    deleted = true,
                    updatedAt = LocalDateTime.now(),
                ),
            )
        } ?: throw CommentNotFoundException

    override suspend fun getComment(id: String): Comment = commentRepository.findById(id) ?: throw CommentNotFoundException

    override suspend fun getCommentsByPost(postId: String): List<Comment> =
        commentRepository.findByPostIdAndDeletedFalseOrderByCreatedAtDesc(postId)

    override suspend fun getCommentsByAuthor(authorId: String): List<Comment> =
        commentRepository.findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId)

    override suspend fun getReplies(parentId: String): List<Comment> =
        commentRepository.findByParentIdAndDeletedFalseOrderByCreatedAtDesc(parentId)
}
