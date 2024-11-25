package msa.user.dto

import jakarta.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)
