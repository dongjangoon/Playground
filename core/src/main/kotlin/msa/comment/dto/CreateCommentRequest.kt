package msa.comment.dto

data class CreateCommentRequest(
    val postId: String,
    val content: String,
    val authorId: String,
    val parentId: String? = null,
)
