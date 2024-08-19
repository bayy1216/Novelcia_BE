package com.reditus.novelcia.domain.user

import com.reditus.novelcia.domain.common.BaseModifiableEntity
import com.reditus.novelcia.global.util.SelfValidating
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true, length = 100)
    var email: String?,

    var encodedPassword: String?,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false)
    var point: Int,

    @Column(nullable = false)
    var memberShipExpiredAt: LocalDateTime,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    val role: Role,
) : BaseModifiableEntity() {

    val isAdmin : Boolean
        get() = role == Role.ADMIN

    val isMemberShipValid : Boolean
        get() = memberShipExpiredAt.isAfter(LocalDateTime.now())

    companion object {
        fun create(command: UserCommand.Create): User {
            require(command.password == null && command.encodedPassword != null)
            return User(
                email = command.email,
                encodedPassword = command.encodedPassword,
                nickname = command.nickname,
                point = 0,
                memberShipExpiredAt = LocalDateTime.now(),
                isDeleted = false,
                role = Role.USER,
            )
        }
        fun fixture(
            id: Long = 0L,
            email: String = "test@a.c",
            encodedPassword: String = "encodedPassword",
            nickname: String = "nickname",
            point: Int = 0,
            memberShipExpiredAt: LocalDateTime = LocalDateTime.now(),
            isDeleted: Boolean = false,
            role: Role = Role.USER,
        ) = User(
            id = id,
            email = email,
            encodedPassword = encodedPassword,
            nickname = nickname,
            point = point,
            memberShipExpiredAt = memberShipExpiredAt,
            isDeleted = isDeleted,
            role = role,
        )
    }
}

enum class Role {
    USER, ADMIN
}

class UserCommand {
    class Create(
        @Email(message = "이메일 형식이 아닙니다.")
        val email: String,
        val password: String?,
        val nickname: String,
        val encodedPassword: String? = null,
    ) : SelfValidating(){
        init {
            require(password != null || encodedPassword != null)
            validateSelf()
        }
        fun copyWith(encodedPassword: String) = Create(
            email = email,
            password = null,
            nickname = nickname,
            encodedPassword = encodedPassword,
        )
    }
}