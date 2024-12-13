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

        if (ex instanceof JwtAuthorizationException) {
            JwtAuthorizationException jwtException = (JwtAuthorizationException) ex;
            ErrorCode errorCode = jwtException.getErrorCode();
            exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorCode.getStatus()));
            errorResponse = new ErrorResponse(jwtException.getMessage(), errorCode.getStatus(), errorCode.getCode());
        } else if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            ErrorCode errorCode = businessException.getErrorCode();
            exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorCode.getStatus()));
            errorResponse = new ErrorResponse(businessException.getMessage(), errorCode.getStatus(), errorCode.getCode());
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse = new ErrorResponse("Unexpected error occurred", 500, "C999");
        }

        byte[] responseBytes;
        try {
            responseBytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            responseBytes = "{\"message\":\"Internal serialization error\",\"status\":500,\"code\":\"C998\"}".getBytes();
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(responseBytes)));
    }

}

