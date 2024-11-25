package msa.user.controller

import jakarta.validation.Valid
import msa.common.dto.ApiResponse
import msa.common.dto.toResponse
import msa.common.exception.DuplicateEmailException
import msa.user.dto.EmailVerificationResponse
import msa.user.dto.LoginRequest
import msa.user.dto.SignupRequest
import msa.user.dto.TokenResponse
import msa.user.dto.UserResponse
import msa.user.service.EmailVerificationService
import msa.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val emailVerificationService: EmailVerificationService
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody request: SignupRequest
    ): ApiResponse<UserResponse> {
        return userService.signup(request).toResponse()
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ApiResponse<TokenResponse> {
        return userService.login(request).toResponse()
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(
        @RequestAttribute userId: Long
    ): ApiResponse<UserResponse> {
        return userService.getCurrentUser(userId).toResponse()
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateProfile(
        @RequestAttribute userId: Long,
        @RequestParam(required = false) nickname: String?,
        @RequestParam(required = false) profileImage: String?
    ): ApiResponse<UserResponse> {
        return userService.updateProfile(userId, nickname, profileImage).toResponse()
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@RequestAttribute userId: Long) {
        userService.deleteUser(userId)
    }

    @PostMapping("/send-verification-email")
    @ResponseStatus(HttpStatus.OK)
    fun sendVerificationEmail(
        @RequestParam email: String,
    ): ApiResponse<EmailVerificationResponse> {
        // 이메일 중복 체크
        if (userService.checkDuplicateEmail(email))
            throw DuplicateEmailException

        return emailVerificationService.sendVerificationEmail(email).toResponse()
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    fun verifyEmail(
        @RequestParam token: String
    ): ApiResponse<EmailVerificationResponse> {
        val verified = emailVerificationService.verifyEmail(token)
        return EmailVerificationResponse(
            message = "이메일 인증이 완료되었습니다",
            email = "",
            expiresIn = 0
        ).toResponse()
    }
}
