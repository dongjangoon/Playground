package msa.gateway.common.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2) // 높은 우선순위로 설정
public class GlobalExceptionHandler implements org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse;
        HttpStatus status;

        if (ex instanceof CustomException customException) {
            errorResponse = handleCustomException(customException);
            status = HttpStatus.valueOf(customException.getErrorCode().getStatus());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = new ErrorResponse("Unexpected error occurred", 500, "C999");
        }

        exchange.getResponse().setStatusCode(status);

        byte[] responseBytes = serializeErrorResponse(errorResponse);

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(responseBytes)));
    }

    private ErrorResponse handleCustomException(CustomException customException) {
        ErrorCode errorCode = customException.getErrorCode();

        return new ErrorResponse(customException.getMessage(), errorCode.getStatus(), errorCode.getCode());
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
