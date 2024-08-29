package com.reditus.novelcia.user.domain.port

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.user.domain.User

interface UserWriter {
    fun save(user: User): User
    fun delete(user: User)

    fun chargePoint(userId: Long, point: PositiveInt)
}