package msa.user.dto

data class EmailVerificationResponse(
    val message: String,
    val email: String,
    val expiresIn: Long
)
