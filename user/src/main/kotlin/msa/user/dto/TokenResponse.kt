package msa.user.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,  // 만료 시간 (millis)
    val refreshTokenExpiresIn: Long,  // 만료 시간 (millis)
    val tokenType: String = "Bearer"
) {
    companion object {
        fun of(
            accessToken: String,
            refreshToken: String,
            accessTokenExpiresIn: Long,
            refreshTokenExpiresIn: Long
        ): TokenResponse = TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = accessTokenExpiresIn,
            refreshTokenExpiresIn = refreshTokenExpiresIn
        )
    }
}
