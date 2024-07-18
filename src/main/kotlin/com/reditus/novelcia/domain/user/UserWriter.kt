package com.reditus.novelcia.domain.user

import com.reditus.novelcia.domain.PositiveInt

interface UserWriter {
    fun save(user: User): User
    fun delete(user: User)

    fun chargePoint(userId: Long, point: PositiveInt)
}