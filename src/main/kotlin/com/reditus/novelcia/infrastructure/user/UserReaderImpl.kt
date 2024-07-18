package com.reditus.novelcia.infrastructure.user

import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.domain.user.UserReader
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Repository

@Repository
class UserReaderImpl(
    private val userRepository: UserRepository
) : UserReader {
    override fun getReferenceById(id: Long): User {
        return userRepository.getReferenceById(id)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun getById(id: Long): User {
        return userRepository.findByIdOrThrow(id)
    }

    override fun existsByNickname(username: String): Boolean {
        return userRepository.existsByNickname(username)
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}