package msa.user.repository

import msa.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmailAndDeletedFalse(email: String): User?
    fun findByNicknameAndDeletedFalse(nickname: String): User?
    fun existsByEmailAndDeletedFalse(email: String): Boolean
    fun existsByNicknameAndDeletedFalse(nickname: String): Boolean

    @Query("SELECT u FROM User u WHERE u.email = :email")
    fun findByEmailIncludedDeleted(@Param("email") email: String): User?

    @Modifying
    @Query("UPDATE User u SET u.deleted = true, u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    fun softDelete(@Param("id") id: Long): Int
}
