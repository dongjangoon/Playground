package msa.post.dto

import msa.common.enum.PostCategory

data class UpdatePostRequest(
    val title: String?,
    val sourceUrl: String?,
    val content: String?,
    val summary: String?,
    val category: PostCategory?,
    val tags: List<String>?,
)
