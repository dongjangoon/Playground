package msa.gateway.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorType {
    // VALIDATION
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, 0x4006L, "유효성 검증에 실패하였습니다."),

    // JWT
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, 0x4101L, "JWT 토큰이 만료되었습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, 0x4102L, "JWT 토큰이 유효하지 않습니다."),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, 0x4103L, "JWT 서명이 유효하지 않습니다."),
    JWT_MISSING(HttpStatus.UNAUTHORIZED, 0x4104L, "JWT 토큰이 존재하지 않습니다."),

    // ADMIN
    ADMIN_ACCESS_ONLY(HttpStatus.FORBIDDEN, 0x4201L, "관리자만 접근할 수 있습니다."),

    // SERVER
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x5001L, "서버 내부 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final long errorCode;
    private final String errorMessage;
}
