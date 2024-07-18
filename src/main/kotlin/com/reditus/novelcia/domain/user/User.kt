package com.reditus.novelcia.domain.user

import com.reditus.novelcia.domain.BaseTimeEntity
import jakarta.persistence.*
import java.time.LocalDate
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
    var point : Int,

    @Column(nullable = false)
    var memberShipExpiredAt : LocalDateTime,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    val role: Role,
) : BaseTimeEntity() {


}

enum class Role {
    USER, ADMIN
}