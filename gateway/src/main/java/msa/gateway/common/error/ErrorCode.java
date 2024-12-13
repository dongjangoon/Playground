package msa.gateway.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xx Client Errors
    UNAUTHORIZED(401, "C401", "Unauthorized access"), // 인증 실패
    FORBIDDEN(403, "C403", "Forbidden access"), // 권한 부족
    BAD_REQUEST(400, "C400", "Bad request"), // 잘못된 요청
    NOT_FOUND(404, "C404", "Resource not found"), // 리소스를 찾을 수 없음
    CONFLICT(409, "C409", "Conflict error"), // 중복 또는 충돌 발생
    VALIDATION_ERROR(422, "C422", "Validation error"), // 유효성 검사 실패

    // JWT Specific Errors
    JWT_EXPIRED(401, "CJWT01", "JWT token is expired"),
    JWT_INVALID(401, "CJWT02", "JWT token is invalid"),
    JWT_SIGNATURE_INVALID(401, "CJWT03", "JWT signature is invalid"),
    JWT_MISSING(401, "CJWT04", "JWT token is missing"),

    // Admin Specific Errors
    ADMIN_ACCESS_ONLY(403, "CADM01", "Admin access only"),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR(500, "C500", "Internal server error"),
    SERVICE_UNAVAILABLE(503, "C503", "Service temporarily unavailable"),
    FRAMEWORK_INTERNAL_ERROR(500, "C001", "Framework internal error"),
    UNDEFINED_ERROR(500, "C002", "Undefined error");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
