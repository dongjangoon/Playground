package msa.user.dto

import msa.common.enum.UserRole
import msa.common.enum.UserStatus
import msa.user.model.User

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val status: UserStatus,
    val role: UserRole
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id!!,
            email = user.email,
            nickname = user.nickname,
            profileImage = user.profileImage,
            status = user.status,
            role = user.role
        )
    }
}
