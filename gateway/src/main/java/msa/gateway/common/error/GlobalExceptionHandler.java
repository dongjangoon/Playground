package msa.gateway.common.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Order(-2) // 높은 우선순위로 설정
public class GlobalExceptionHandler implements org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return Mono.just(ex)
                .flatMap(CustomException::fromThrowable) // CustomException 내부 메서드로 위임
                .flatMap(customException -> handleError(exchange, customException))
                .onErrorResume(e -> handleError(exchange, null));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, CustomException customException) {
        ErrorResponse errorResponse;

        if (customException != null) {
            // CustomException 기반 에러 응답 생성
            errorResponse = new ErrorResponse(
                    customException.getMessage(),
                    customException.getErrorType().getHttpStatus(),
                    customException.getErrorType().getErrorMessage()
            );
        } else {
            // 일반 에러 응답 생성
            errorResponse = new ErrorResponse(
                    ErrorType.INTERNAL_SERVER_ERROR.getErrorMessage(),
                    ErrorType.INTERNAL_SERVER_ERROR.getHttpStatus(),
                    String.format("E%04d", ErrorType.INTERNAL_SERVER_ERROR.getErrorCode())
            );
        }

        return errorResponse.writeToExchange(exchange, objectMapper);
    }
}
