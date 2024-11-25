package msa.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import msa.common.enum.UserRole
import msa.common.enum.UserStatus
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@SQLRestriction("deleted = false")  // Soft delete, 조회 시 deleted = false인 데이터만 조회
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false, unique = true)
    var nickname: String,

    var profileImage: String? = null,

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER,

    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime? = null,

    @Column(nullable = false)
    var deleted: Boolean = false,

    var deletedAt: LocalDateTime? = null
)
