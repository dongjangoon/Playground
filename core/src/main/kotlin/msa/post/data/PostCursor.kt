package msa.post.data

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64

data class PostCursor(
    val id: String,
    val createdAt: LocalDateTime,
    val recommendCount: Long,
) {
    fun encode(): String =
        Base64.getEncoder().encodeToString(
            "$id:${createdAt.toEpochSecond(ZoneOffset.UTC)}:$recommendCount".toByteArray(),
        )

    companion object {
        fun decode(cursor: String): PostCursor {
            val decoded = String(Base64.getDecoder().decode(cursor)).split(":")
            return PostCursor(
                id = decoded[0],
                createdAt = LocalDateTime.ofEpochSecond(decoded[1].toLong(), 0, ZoneOffset.UTC),
                recommendCount = decoded[2].toLong(),
            )
        }
    }
}
