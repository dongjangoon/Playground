package msa.message.consumer

import msa.message.event.EmailVerificationEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailConsumer(
    private val mailSender: JavaMailSender
) {
    @KafkaListener(topics = ["email-verification"], groupId = "email-service")
    fun handEmailVerification(event: EmailVerificationEvent) {
        val message = SimpleMailMessage().apply {
            setTo(event.email)
            subject = "[Comfortable] 이메일 인증을 완료해주세요"
            text = """
                안녕하세요. Comfortable Team입니다.
                
                이메일 인증을 완료하려면 아래 링크를 클릭하세요.
                
                http://localhost:8081/api/v1/users/verify-email?token=${event.token}
                
                감사합니다.
            """.trimIndent()
        }

        mailSender.send(message)
    }
}
