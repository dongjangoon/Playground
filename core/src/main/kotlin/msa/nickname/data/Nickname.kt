package msa.nickname.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("nicknames")
@CompoundIndex(name = "idx_adjective_noun", def = "{'adjective': 1, 'noun': 1}", unique = true)
data class Nickname(
    @Id
    val id: String? = null,
    @Indexed
    val adjective: String,
    @Indexed
    val noun: String,
    @Indexed(unique = true)
    val combinedName: String = "$adjective$noun",
    val isUsed: Boolean = false,
    @Indexed
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun create(
            adjective: String,
            noun: String,
        ) = Nickname(
            adjective = adjective.trim(),
            noun = noun.trim(),
        )
    }
}
