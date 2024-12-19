package msa.gateway.common.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
    }
}
