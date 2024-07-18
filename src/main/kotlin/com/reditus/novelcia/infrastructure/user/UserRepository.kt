package com.reditus.novelcia.infrastructure.user

import com.reditus.novelcia.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByNickname(nickname: String): Boolean
    fun existsByEmail(email: String): Boolean

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.point = u.point + :point WHERE u.id = :userId")
    fun chargePoint(
        @Param("userId")
        userId: Long,
        @Param("point")
        point: Int,
    ): Int
}