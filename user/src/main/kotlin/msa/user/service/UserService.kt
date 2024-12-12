package msa.user.service

import jakarta.transaction.Transactional
import msa.common.enum.UserRole
import msa.common.enum.UserStatus
import msa.common.exception.DuplicateEmailException
import msa.common.exception.DuplicateNicknameException
import msa.common.exception.EmailNotVerifiedException
import msa.common.exception.InvalidCredentialsException
import msa.common.exception.UserNotActiveException
import msa.config.jwt.JwtUtil
import msa.user.dto.LoginRequest
import msa.user.dto.SignupRequest
import msa.user.dto.TokenResponse
import msa.user.dto.UserResponse
import msa.user.model.User
import msa.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface UserService {
    fun signup(request: SignupRequest): UserResponse
    fun login(request: LoginRequest): TokenResponse
    fun getCurrentUser(userId: Long): UserResponse
    fun updateProfile(
        userId: Long,
        nickname: String?,
        profileImage: String?
    ): UserResponse
    fun updateRole(userId: Long, role: UserRole): UserResponse
    fun updateStatus(userId: Long, status: UserStatus): UserResponse
    fun deleteUser(userId: Long)
    fun checkDuplicateEmail(email: String): Boolean
    fun checkDuplicateNickname(nickname: String): Boolean
    fun verifyEmail(email: String): Unit
}

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailVerificationService: EmailVerificationService,
    private val jwtUtil: JwtUtil
) : UserService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun signup(request: SignupRequest): UserResponse {
        // 삭제된 계정을 포함하여 이메일 중복 확인
        userRepository.findByEmailIncludedDeleted(request.email)?.let {
            if (!it.deleted) {
                throw DuplicateEmailException
            } else {
                // 삭제된 계정의 경우 재사용 가능하도록 복구
                it.deleted = false
                it.deletedAt = null
                return UserResponse.from(userRepository.save(it))
                    .also { log.info("User restored: ${it.id}") }
            }
        }

        // TODO 이메일 인증 확인 : 테스트과정에서는 잠시 주석처리
//        if (!emailVerificationService.isEmailVerified(request.email))
//            throw EmailNotVerifiedException

        if (checkDuplicateNickname(request.nickname)) {
            throw DuplicateNicknameException
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname,
            status = UserStatus.ACTIVE
        )

        return UserResponse.from(userRepository.save(user))
            .also { log.info("User created: ${user.id}") }
    }

    override fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmailAndDeletedFalse(request.email)
            ?: throw InvalidCredentialsException

        if (!passwordEncoder.matches(request.password, user.password))
            throw InvalidCredentialsException

        // PENDING 상태인 계정은 로그인 불가
        if (user.status == UserStatus.PENDING)
            throw EmailNotVerifiedException

        if (user.status != UserStatus.ACTIVE)
            throw UserNotActiveException

        return jwtUtil.generateTokens(user.id!!)
            .also { log.info("User logged in: ${user.id}") }
    }

    override fun getCurrentUser(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .filter{ !it.deleted }
            .orElseThrow { throw InvalidCredentialsException }

        return UserResponse.from(user)
    }

    override fun checkDuplicateEmail(email: String): Boolean {
        return userRepository.existsByEmailAndDeletedFalse(email)
    }

    override fun checkDuplicateNickname(nickname: String): Boolean {
        return userRepository.existsByNicknameAndDeletedFalse(nickname)
    }

    override fun updateProfile(
        userId: Long,
        nickname: String?,
        profileImage: String?
    ): UserResponse {
        val user = userRepository.findById(userId)
            .filter { !it.deleted }
            .orElseThrow { throw InvalidCredentialsException }

        nickname?.let {
            if (it != user.nickname && checkDuplicateNickname(it)) {
                throw DuplicateNicknameException
            }
            user.nickname = it
        }

        user.profileImage = profileImage ?: user.profileImage
        user.updatedAt = LocalDateTime.now()

        return UserResponse.from(userRepository.save(user))
            .also { log.info("User updated: $userId") }
    }

    override fun updateRole(userId: Long, role: UserRole): UserResponse {
        val user = userRepository.findById(userId)
            .filter { !it.deleted }
            .orElseThrow { throw InvalidCredentialsException }

        user.role = role
        user.updatedAt = LocalDateTime.now()

        return UserResponse.from(userRepository.save(user))
            .also { log.info("User updated: $userId") }
    }

    override fun updateStatus(userId: Long, status: UserStatus): UserResponse {
        val user = userRepository.findById(userId)
            .filter { !it.deleted }
            .orElseThrow { throw InvalidCredentialsException }

        user.status = status
        user.updatedAt = LocalDateTime.now()

        return UserResponse.from(userRepository.save(user))
            .also { log.info("User updated: $userId") }
    }

    override fun deleteUser(userId: Long) {
        val user = userRepository.findById(userId)
            .filter { !it.deleted }
            .orElseThrow { throw InvalidCredentialsException }

        userRepository.softDelete(userId)
            .also { log.info("User deleted: $userId") }
    }

    @Transactional
    override fun verifyEmail(email: String) {
        val user = userRepository.findByEmailAndDeletedFalse(email)
            ?: throw InvalidCredentialsException

        user.status = UserStatus.ACTIVE
        user.updatedAt = LocalDateTime.now()

        userRepository.save(user)
    }
}
