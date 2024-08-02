package com.reditus.novelcia.domain.user

import com.reditus.novelcia.global.util.TxContext
import java.time.LocalDateTime

class UserModel(
    val id: Long,
    val email: String?,
    val nickname: String,
    val point: Int,
    val memberShipExpiredAt: LocalDateTime,
    val role: Role,
) {
    companion object {
        fun from(user: User): TxContext.() -> UserModel = {
            UserModel(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                point = user.point,
                memberShipExpiredAt = user.memberShipExpiredAt,
                role = user.role
            )
        }
    }
}