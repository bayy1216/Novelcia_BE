package com.reditus.novelcia.domain.user.port

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.user.User

interface UserWriter {
    fun save(user: User): User
    fun delete(user: User)

    fun chargePoint(userId: Long, point: PositiveInt)
}