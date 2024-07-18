package com.reditus.novelcia.domain.user

interface UserReader {
    fun findByEmail(email: String): User?
    fun getById(id: Long): User
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}