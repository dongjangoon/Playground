package msa.message.event

data class EmailVerificationEvent(
    val email: String,
    val token: String,
    val messageType: String
)
