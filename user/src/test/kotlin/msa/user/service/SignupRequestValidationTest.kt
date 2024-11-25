package msa.user.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import jakarta.validation.Validation
import jakarta.validation.Validator
import msa.user.dto.SignupRequest

class SignupRequestValidationTest : BehaviorSpec({
    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    given("a signup request") {
        `when`("password is valid") {
            val invalidPasswords = listOf(
                "password" to "비밀번호는 숫자, 영문 대문자, 특수문자를 포함해야 합니다.",
                "password123" to "비밀번호는 영문 대문자, 특수문자를 포함해야 합니다.",
                "PASSWORD123" to "비밀번호는 영문 소문자, 특수문자를 포함해야 합니다.",
                "Password123" to "비밀번호는 특수문자를 포함해야 합니다.",
                "password!@#" to "비밀번호는 영문 대문자를 포함해야 합니다.",
                "Password!@#" to "비밀번호는 숫자를 포함해야 합니다."
            )

            then("validation should fail with appropriate message") {
                invalidPasswords.forEach { (password, reason) ->
                    val request = SignupRequest(
                        email = "test@example.com",
                        password = password,
                        nickname = "nickname"
                    )

                    val violations = validator.validate(request)

                    violations.shouldNotBeEmpty()
                }
            }
        }

        `when`("password is valid") {
            val validPasswords = listOf(
                "Password123!",
                "Test@password123",
                "1234@Password",
                "Complex!Pass234"
            )

            then("validation should pass") {
                validPasswords.forEach { password ->
                    val request = SignupRequest(
                        email = "test@example.com",
                        password = password,
                        nickname = "nickname"
                    )

                    val violations = validator.validate(request)

                    violations.shouldBeEmpty()
                }
            }
        }

        `when`("password length is invalid") {
            then("validation should fail for too short and too long passwords") {
                val tooShortPass = "P@ss1"
                val tooLongPass = "P@ssword12".repeat(3)

                listOf(tooShortPass, tooLongPass).forEach { password ->
                    val request = SignupRequest(
                        email = "test@example.com",
                        password = password,
                        nickname = "nickname"
                    )

                    val violations = validator.validate(request)

                    violations.shouldNotBeEmpty()
                }
            }
        }
    }
})
