package msa.common.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
    val httpStatus: HttpStatus,
    val errorCode: Long,
    val errorMessage: String,
    val displayMessage: String = "현재 서비스 이용이 원활하지 않습니다.",
) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0x0000, "API 호출에 실패하였습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 0x0001, "입력값이 올바르지 않습니다."),

    NICKNAME_NOT_FOUND(HttpStatus.NOT_FOUND, 0x1001, "해당 닉네임이 존재하지 않습니다."),
    NO_UNUSED_NICKNAME_AVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR, 0x1002, "현재 사용 가능한 닉네임이 존재하지 않습니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 0x1003, "이미 존재하는 닉네임입니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 0x2001, "해당 게시글이 존재하지 않습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 0x3001, "해당 댓글이 존재하지 않습니다."),

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 0x4001, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, 0x4002, "이미 존재하는 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, 0x4003, "이메일 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, 0x4004, "비활성화된 계정입니다. 관리자에게 문의하세요."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, 0x4005, "이메일 인증이 필요합니다."),
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, 0x4006, "유효하지 않은 인증 토큰입니다."),
}
