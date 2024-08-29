package com.reditus.novelcia.user.infrastructure.adapter

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.domain.port.UserWriter
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class UserWriterImpl(
    private val userRepository: UserRepository
) : UserWriter {
    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun delete(user: User) {
        userRepository.delete(user)
    }

    override fun chargePoint(userId: Long, point: PositiveInt) {
        val result = userRepository.chargePoint(userId, point.value)
        if (result == 0) {
            throw IllegalArgumentException("User not found")
        }
    }
}