package msa.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import msa.common.exception.ErrorType
import msa.common.exception.NicknameException

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class ApiResponse<T> {
    data class Success<T>(
        val data: T,
    ) : ApiResponse<T>()

    data class Error<T>(
        val errorCode: Long,
        val message: String,
        val displayMessage: String,
        val detail: Any? = null,
    ) : ApiResponse<T>()
}

fun <T> T.toResponse(): ApiResponse<T> {
    return ApiResponse.Success(this)
}

fun ErrorType.toResponse(): ApiResponse<Nothing> {
    return ApiResponse.Error(
        errorCode = this.errorCode,
        message = this.errorMessage,
        displayMessage = this.displayMessage,
    )
}

fun NicknameException.toResponse(): ApiResponse<Nothing> {
    return ApiResponse.Error(
        errorCode = this.error.errorCode,
        message = this.errorMessage,
        displayMessage = this.displayMessage,
        detail = this.detail,
    )
}
