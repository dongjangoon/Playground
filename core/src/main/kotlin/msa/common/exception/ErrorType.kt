package msa.common.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val httpStatus: HttpStatus,
    val errorCode: Long,
    val errorMessage: String,
    val displayMessage: String = "현재 서비스 이용이 원활하지 않습니다.",
) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x0000, "API 호출에 실패하였습니다."),

    NICKNAME_NOT_FOUND(HttpStatus.NOT_FOUND, 0x1001, "해당 닉네임이 존재하지 않습니다."),
    NO_UNUSED_NICKNAME_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, 0x1002, "현재 사용 가능한 닉네임이 존재하지 않습니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 0x1003, "이미 존재하는 닉네임입니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 0x2001, "해당 게시글이 존재하지 않습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 0x3001, "해당 댓글이 존재하지 않습니다."),

    ROBOTS_NOT_FOUND(HttpStatus.NOT_FOUND, 0x4001, "robots.txt가 존재하지 않습니다."),
    ROBOTS_NOT_ALLOWED(HttpStatus.FORBIDDEN, 0x4002, "robots.txt에 의해 크롤링이 금지되었습니다."),
    URL_ALREADY_CRAWLED(HttpStatus.BAD_REQUEST, 0x4003, "이미 크롤링한 URL입니다."),
}
