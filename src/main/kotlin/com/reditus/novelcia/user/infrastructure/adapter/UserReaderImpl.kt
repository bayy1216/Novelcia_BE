package com.reditus.novelcia.user.infrastructure.adapter

import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.application.port.UserReader
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.user.infrastructure.UserRepository
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