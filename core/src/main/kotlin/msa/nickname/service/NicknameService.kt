package msa.nickname.service

import msa.common.exception.NicknameAlreadyExistsException
import msa.common.exception.NicknameNotFoundException
import msa.common.exception.NoUnusedNicknameAvailableException
import msa.nickname.data.Nickname
import msa.nickname.repository.NicknameRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface NicknameService {
    suspend fun generateNickname(): Nickname

    suspend fun generateNicknameWithWords(
        adjective: String,
        noun: String,
    ): Nickname

    suspend fun markAsUsed(id: String): Nickname

    suspend fun getUnusedNickname(): Nickname
}

@Service
class NicknameServiceImpl(
    private val nicknameRepository: NicknameRepository,
    private val wordProvider: WordProvider,
) : NicknameService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun generateNickname(): Nickname {
        return generateNicknameWithWords(
            wordProvider.getRandomAdjective(),
            wordProvider.getRandomNoun(),
        ) ?: generateNickname() // 중복 시 재귀적으로 다시 시도
    }

    override suspend fun generateNicknameWithWords(
        adjective: String,
        noun: String,
    ): Nickname {
        val nickname = Nickname.create(adjective, noun)
        if (nicknameRepository.existsByCombinedName(nickname.combinedName)) throw NicknameAlreadyExistsException
        return nicknameRepository.save(nickname)
    }

    override suspend fun markAsUsed(id: String): Nickname {
        return nicknameRepository.findById(id)?.let { nickname ->
            nicknameRepository.save(
                nickname.copy(
                    isUsed = true,
                    updatedAt = LocalDateTime.now(),
                ),
            )
        } ?: throw NicknameNotFoundException
    }

    override suspend fun getUnusedNickname(): Nickname {
        return nicknameRepository.findRandomUnusedNickname()
            ?: throw NoUnusedNicknameAvailableException
    }
}
