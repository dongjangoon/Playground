package msa.config.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import msa.user.dto.TokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil(
    @Value("\${spring.security.jwt.secret}")
    private val secretKey: String,

    @Value("\${spring.security.jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,

    @Value("\${spring.security.jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateTokens(userId: Long): TokenResponse {
        val now = System.currentTimeMillis()

        val accessToken = generateToken(userId, now, accessTokenExpiration)
        val refreshToken = generateToken(userId, now, refreshTokenExpiration)

        return TokenResponse.of(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = accessTokenExpiration,
            refreshTokenExpiresIn = refreshTokenExpiration
        )
    }

    private fun generateToken(userId: Long, now: Long, expiration: Long): String =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + expiration))
            .signWith(key)
            .compact()


    fun validateToken(token: String): Boolean = runCatching {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
        true
    }.getOrDefault(false)

    fun getUserIdFromToken(token: String): Long =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
            .toLong()
}
