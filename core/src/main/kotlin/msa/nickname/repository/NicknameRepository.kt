package msa.nickname.repository

import kotlinx.coroutines.flow.Flow
import msa.nickname.data.Nickname
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NicknameRepository : CoroutineCrudRepository<Nickname, String> {
    fun findByIsUsed(isUsed: Boolean): Flow<Nickname>

    suspend fun countByIsUsed(isUsed: Boolean): Long

    suspend fun existsByCombinedName(combinedName: String): Boolean

    @Aggregation(
        pipeline = [
            "{ '\$match': { 'isUsed': false } }",
            "{ '\$sample': { 'size': 1 } }",
        ],
    )
    suspend fun findRandomUnusedNickname(): Nickname?
}
