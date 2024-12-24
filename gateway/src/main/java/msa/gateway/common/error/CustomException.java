package msa.gateway.common.error;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
    }

    public static Mono<CustomException> fromThrowable(Throwable ex) {
        if (ex instanceof CustomException) {
            return Mono.just((CustomException) ex);
        } else {
            return Mono.error(new IllegalArgumentException("CustomException 으로 변환할 수 없음"));
        }
    }
}
