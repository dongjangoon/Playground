package msa.handler

import msa.nickname.dto.GenerateNicknameRequest
import msa.nickname.service.NicknameService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class NicknameHandler(
    private val nicknameService: NicknameService,
) {
    suspend fun generateNickname(request: ServerRequest): ServerResponse {
        val nickname = nicknameService.generateNickname()
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(nickname)
    }

    suspend fun generateCustomNickname(request: ServerRequest): ServerResponse {
        val body = request.awaitBody<GenerateNicknameRequest>()
        val nickname =
            nicknameService.generateNicknameWithWords(
                body.adjective,
                body.noun,
            ) ?: IllegalStateException("Failed to generate nickname")

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(nickname)
    }

    suspend fun markAsUsed(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val nickname = nicknameService.markAsUsed(id)
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(nickname)
    }

    suspend fun getUnusedNickname(request: ServerRequest): ServerResponse {
        val nickname = nicknameService.getUnusedNickname()
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(nickname)
    }
}
