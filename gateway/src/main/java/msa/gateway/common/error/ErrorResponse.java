package msa.gateway.common.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Data
public class ErrorResponse {
    private String message;
    private HttpStatus status;
    private String code;

    public ErrorResponse(String message, HttpStatus status, String code) {
        this.message = message;
        this.status = status;
        this.code = code;
    }

    public ErrorResponse(CustomException customException) {
        this.message = customException.getMessage();
        this.status = customException.getErrorType().getHttpStatus();
        this.code = customException.getErrorType().getErrorMessage();
    }

    public static ErrorResponse notCustomError() {
        return new ErrorResponse(
                ErrorType.INTERNAL_SERVER_ERROR.getErrorMessage(),
                ErrorType.INTERNAL_SERVER_ERROR.getHttpStatus(),
                String.format("E%04d", ErrorType.INTERNAL_SERVER_ERROR.getErrorCode())
        );
    }

    public Mono<Void> writeToExchange(ServerWebExchange exchange, ObjectMapper objectMapper) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(this)) // 현재 ErrorResponse 객체를 JSON으로 직렬화
                .flatMap(responseBytes -> {
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    exchange.getResponse().setStatusCode(this.status);
                    return exchange.getResponse().writeWith(
                            Mono.just(exchange.getResponse().bufferFactory().wrap(responseBytes))
                    );
                });
    }
}
