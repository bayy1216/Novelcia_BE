package com.reditus.novelcia.domain.user

import com.reditus.novelcia.domain.PositiveInt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userReader: UserReader,
    private val userWriter: UserWriter
) {
    @Transactional(readOnly = true)
    fun getUserModelById(userId: Long) : UserModel {
        val user = userReader.getById(userId)
        return UserModel.from(user)
    }

    @Transactional
    fun chargePoint(userId: Long, point: PositiveInt) {
        userWriter.chargePoint(userId, point)
    }
}