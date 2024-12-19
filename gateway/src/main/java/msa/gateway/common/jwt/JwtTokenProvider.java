package msa.gateway.common.jwt;

import msa.gateway.common.error.CustomException;
import msa.gateway.common.error.ErrorType;
import org.springframework.http.HttpHeaders;

import java.util.Optional;
import java.util.function.Function;

public class JwtTokenProvider {

    // 토큰 추출 함수를 정적 함수형으로 작성
    public static final Function<HttpHeaders, String> TOKEN_EXTRACTOR = headers -> {
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        return Optional.ofNullable(authHeader)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7)) // "Bearer " 이후의 토큰만 추출
                .orElseThrow(() -> new CustomException(ErrorType.UNAUTHORIZED));
    };
}
