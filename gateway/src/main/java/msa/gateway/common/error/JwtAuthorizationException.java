package msa.gateway.common.error;

import lombok.Getter;

@Getter
public class JwtAuthorizationException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtAuthorizationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
