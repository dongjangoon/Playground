package msa.gateway.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JwtUtil {

    public static String secretKey = JwtConstants.key;

    // 헤더에 "Bearer XXX" 형식으로 담겨온 토큰을 추출
    public static String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format.");
        }
        return header.split(" ")[1];
    }

    // 토큰 검증
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claims = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(JwtUtil.secretKey.getBytes(StandardCharsets.UTF_8));
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomExpiredJwtException("토큰이 만료되었습니다", expiredJwtException);
        } catch (Exception e) {
            throw new CustomJwtException("토큰 검증 실패");
        }
        return claims;
    }

    // 인증 정보 획득
    public static Authentication getAuthentication(String token) {
        Map<String, Object> claims = validateToken(token);

        // 클레임에서 사용자 정보 추출
        String email = (String) claims.get("email");
        String role = (String) claims.get("role");

        // 권한 생성
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(role)
        );

        // 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
