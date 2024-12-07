package msa.content.enum

enum class BlogType {
    KAKAO_TECH,
    NAVER_D2,
    LINE_ENGINEERING,
    WOOWA_TECH,
    ETC
    ;

    companion object {
        fun fromString(value: String): BlogType {
            return valueOf(value.uppercase())
        }
    }
}
