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

object CommentNotFoundException : NicknameException(ErrorType.COMMENT_NOT_FOUND)

object RobotsNotFoundException : NicknameException(ErrorType.ROBOTS_NOT_FOUND)

object RobotsNotAllowedException : NicknameException(ErrorType.ROBOTS_NOT_ALLOWED)

object UrlAlreadyCrawledException : NicknameException(ErrorType.URL_ALREADY_CRAWLED)
