package com.reditus.novelcia.infrastructure.user

import com.reditus.novelcia.domain.PositiveInt
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.domain.user.port.UserWriter
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