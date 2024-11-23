package msa.common.exception

open class NicknameException(
    val error: ErrorType = ErrorType.DEFAULT_ERROR,
    val errorMessage: String = error.errorMessage,
    val displayMessage: String = error.displayMessage,
    val detail: Any? = null,
) : RuntimeException(errorMessage)

object NicknameNotFoundException : NicknameException(ErrorType.NICKNAME_NOT_FOUND)

object NoUnusedNicknameAvailableException : NicknameException(ErrorType.NO_UNUSED_NICKNAME_AVAILABLE)

object NicknameAlreadyExistsException : NicknameException(ErrorType.NICKNAME_ALREADY_EXISTS)

object PostNotFoundException : NicknameException(ErrorType.POST_NOT_FOUND)
