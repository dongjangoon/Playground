package msa.gateway.common.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNullApi;
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
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        CustomException customException = (CustomException) ex;
        ErrorResponse errorResponse = handleCustomException(customException);
        HttpStatus status = customException.getErrorType().getHttpStatus();

        exchange.getResponse().setStatusCode(status);

        byte[] responseBytes = serializeErrorResponse(errorResponse);

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(responseBytes)));
    }

    private ErrorResponse handleCustomException(CustomException customException) {
        ErrorType errorType = customException.getErrorType();

        return new ErrorResponse(customException.getMessage(), errorType.getHttpStatus(), errorType.getErrorMessage());
    }

    private byte[] serializeErrorResponse(ErrorResponse errorResponse) {
        try {
            return objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            String fallbackError = "{\"message\":\"Internal serialization error\",\"status\":500,\"code\":\"C998\"}";
            return fallbackError.getBytes();
        }
    }
}
