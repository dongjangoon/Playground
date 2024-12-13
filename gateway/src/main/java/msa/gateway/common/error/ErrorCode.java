package msa.gateway.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
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
