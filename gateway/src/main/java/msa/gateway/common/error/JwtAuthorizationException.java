package msa.gateway.common.error;

public class JwtAuthorizationException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtAuthorizationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
