package msa.user.service

import msa.common.exception.InvalidVerificationTokenException
import msa.message.event.EmailVerificationEvent
import msa.user.dto.EmailVerificationResponse
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class EmailVerificationService(
    private val kafkaTemplate: KafkaTemplate<String, EmailVerificationEvent>,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        const val EMAIL_VERIFICATION_TOPIC = "email-verification"
        const val TOKEN_PREFIX = "email:verification:"
        val TOKEN_EXPIRATION = Duration.ofMinutes(5)
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 회원가입 인증 메일 발송
     */
    fun sendVerificationEmail(email: String): EmailVerificationResponse {
        val token = UUID.randomUUID().toString()
        sendVerificationToken(email, token)
        sendEmailEvent(email, token)

        return EmailVerificationResponse(
            message = "인증 메일이 발송되었습니다.",
            email = email,
            expiresIn = TOKEN_EXPIRATION.toMinutes()
        )
    }

    /**
     * 이메일 인증 토큰 검증
     */
    fun verifyEmail(token: String): Boolean {
        val key = "$TOKEN_PREFIX$token"
        val email = redisTemplate.opsForValue().get(key) ?: throw InvalidVerificationTokenException

        redisTemplate.delete(key)
        redisTemplate.opsForValue().set(
            "verified:email:$email",
            token,
            Duration.ofDays(1)
        )

        return true
    }

    /**
     * 이메일 인증 여부 확인
     */
    fun isEmailVerified(email: String): Boolean {
        return redisTemplate.hasKey("verified:email:$email")
    }

    private fun sendVerificationToken(email: String, token: String) {
        redisTemplate.opsForValue().set(
            "$TOKEN_PREFIX$token",
            email,
            TOKEN_EXPIRATION
        )
    }

    private fun sendEmailEvent(email: String, token: String) {
        val event = EmailVerificationEvent(
            email = email,
            token = token,
            messageType = "VERIFICATION"
        )

        kafkaTemplate.send(EMAIL_VERIFICATION_TOPIC, event)
            .also { log.info("인증 메일이 발송되었습니다: $email") }
    }
}
