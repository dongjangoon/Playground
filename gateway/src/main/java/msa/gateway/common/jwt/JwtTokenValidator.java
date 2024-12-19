package msa.gateway.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;

public class JwtTokenValidator {

    // 함수형으로 토큰 검증하는 부분 리팩토링
    public static final BiFunction<String, String, Claims> VALIDATE_TOKEN = (token, secretKey) ->
            Optional.ofNullable(secretKey)
                    .map(key -> {
                        SecretKey secret = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
                        return Jwts.parserBuilder()
                                .setSigningKey(secret)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
                    })
                    .orElseThrow(() -> new CustomException(ErrorType.UNAUTHORIZED));
}
