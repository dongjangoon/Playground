package msa.handler

import msa.common.exception.ErrorType
import msa.common.exception.NicknameException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    data class ErrorBody(
        val errorCode: Long,
        val message: String,
        val displayMessage: String,
        val detail: Any? = null,
    )

    @ExceptionHandler(NicknameException::class)
    fun handleNicknameException(e: NicknameException): ResponseEntity<ErrorBody> {
        return ResponseEntity
            .status(e.error.httpStatus)
            .body(
                ErrorBody(
                    e.error.errorCode,
                    e.errorMessage,
                    e.displayMessage,
                    e.detail,
                ),
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorBody> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorBody(
                    ErrorType.INVALID_INPUT.errorCode,
                    ErrorType.INVALID_INPUT.errorMessage,
                    ErrorType.INVALID_INPUT.displayMessage,
                    detail = e.bindingResult.fieldErrors.map {
                        mapOf(
                            "field" to it.field,
                            "message" to (it.defaultMessage ?: "유효하지 않은 값입니다."),
                        )
                    }
                ),
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorBody> {
        log.error("Unhandled exception occurred", e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorBody(
                    ErrorType.DEFAULT_ERROR.errorCode,
                    ErrorType.DEFAULT_ERROR.errorMessage,
                    ErrorType.DEFAULT_ERROR.displayMessage,
                ),
            )
    }
}
