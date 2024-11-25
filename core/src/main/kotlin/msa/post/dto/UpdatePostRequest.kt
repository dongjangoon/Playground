package msa.post.dto

import msa.common.enum.PostCategory

data class UpdatePostRequest(
    val title: String?,
    val content: String?,
    val summary: String?,
    val category: PostCategory?,
    val tags: List<String>?,
)
