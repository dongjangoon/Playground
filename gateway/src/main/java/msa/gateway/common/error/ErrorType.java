package msa.gateway.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorType {

    // CLIENT
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 0x4001L, "인증에 실패하였습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 0x4002L, "접근 권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 0x4003L, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 0x4004L, "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, 0x4005L, "데이터 충돌이 발생하였습니다."),

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
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x5001L, "서버 내부 오류가 발생하였습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 0x5002L, "현재 서비스 이용이 불가능합니다."),
    FRAMEWORK_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x5003L, "프레임워크 내부 오류가 발생하였습니다."),
    UNDEFINED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x5004L, "정의되지 않은 오류가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final long errorCode;
    private final String errorMessage;
}
