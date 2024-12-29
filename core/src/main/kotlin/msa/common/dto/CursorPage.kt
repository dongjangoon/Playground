package msa.common.dto

data class CursorPage<T>(
    val content: List<T>,
    val hasNext: Boolean,
    val cursor: String?,
)
