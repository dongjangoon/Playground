package msa.gateway.common.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
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

        if (customException != null) errorResponse = new ErrorResponse(customException);
        else errorResponse = ErrorResponse.notCustomError();

        return errorResponse.writeToExchange(exchange, objectMapper);
    }
}
