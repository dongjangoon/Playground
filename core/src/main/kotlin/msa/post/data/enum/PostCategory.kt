package msa.common.enum

enum class PostCategory {
    BACKEND,
    FRONTEND,
    AI,
    DATA,
    DEVOPS,
    ARCHITECTURE,
    CAREER,
    NEWS,
    ;

    companion object {
        fun fromString(value: String): PostCategory {
            return valueOf(value.uppercase())
        }
    }
}
