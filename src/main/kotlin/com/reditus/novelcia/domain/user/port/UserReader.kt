package com.reditus.novelcia.domain.user.port

import com.reditus.novelcia.domain.user.User

interface UserReader {
    fun getReferenceById(id: Long): User
    fun findByEmail(email: String): User?
    fun getById(id: Long): User
    fun existsByNickname(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}