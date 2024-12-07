package msa.content.dto

import msa.common.enum.PostCategory
import msa.content.enum.BlogType

data class SourceUrl(
    val url: String,
    val category: PostCategory,
    val type: BlogType,
)
