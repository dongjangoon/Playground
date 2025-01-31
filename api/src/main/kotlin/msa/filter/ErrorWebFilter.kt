package msa.filter

import com.fasterxml.jackson.databind.ObjectMapper
import msa.common.exception.ErrorType
import msa.common.exception.NicknameException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class ErrorWebFilter(
    private val objectMapper: ObjectMapper,
) : WebFilter {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        return chain.filter(exchange)
            .onErrorResume { throwable ->
                val errorBody: ErrorBody
                val httpStatusCode: HttpStatusCode
                when (throwable) {
                    is NicknameException -> {
                        httpStatusCode = throwable.error.httpStatus
                        errorBody = makeErrorBody(throwable)
                    }
                    is ResponseStatusException -> {
                        httpStatusCode = throwable.statusCode
                        errorBody =
                            makeErrorBody(
                                NicknameException(errorMessage = throwable.body.title ?: ErrorType.DEFAULT_ERROR.errorMessage),
                            )
                    }
                    else -> {
                        log.error(throwable.message, throwable)
                        httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR
                        errorBody = makeErrorBody(NicknameException())
                    }
                }

                exchange.response.statusCode = httpStatusCode
                exchange.response.headers.contentType = MediaType.APPLICATION_JSON
                exchange.response.writeWith(
                    Mono.just(
                        exchange.response
                            .bufferFactory()
                            .wrap(objectMapper.writeValueAsBytes(errorBody)),
                    ),
                )
            }
    }

    private fun makeErrorBody(exception: NicknameException): ErrorBody {
        return ErrorBody(exception.error.errorCode, exception.errorMessage, exception.displayMessage, exception.detail)
    }
}

private data class ErrorBody(
    val errorCode: Long,
    val message: String,
    val displayMessage: String,
    val detail: Any? = null,
)
