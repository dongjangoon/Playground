package msa.post.dto

import msa.common.enum.PostCategory

data class CreatePostRequest(
    val title: String,
    val content: String,
    val summary: String,
    val category: PostCategory,
    val authorId: String,
    val tags: List<String>
)
