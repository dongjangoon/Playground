package msa.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:Email
    val email: String,

    @field:Size(min = 8, max = 20)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*(),.?\":{}|<>]).*$",
        message = "비밀번호는 숫자, 영문 대소문자, 특수문자를 포함하는 8~20자의 문자열이어야 합니다."
    )
    val password: String,

    @field:Size(min = 2, max = 20)
    val nickname: String
)
