package com.reditus.novelcia.global.util

import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import com.reditus.novelcia.infrastructure.user.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest
class TxTest @Autowired constructor(
    private val testService: TestService,
    private val userRepository: UserRepository,
) {
    private val userEmail = "tt@a.c"

    @AfterEach
    fun tearDown() {
        userRepository.findByEmail(userEmail)?.let {
            userRepository.delete(it)
        }

    }

    @Test
    fun `단일 트랜잭션으로 실행시 롤백이 정상적으로 수행된다`() {
        val user = User.fixture(email = userEmail)
        userRepository.save(user)

        try {
            testService.addUserPointWithAllTx(user.id, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val foundUser = userRepository.findById(user.id).get()
        assertEquals(foundUser.point, 0)
    }

    @Test
    fun `private내부 트랜잭션에서 롤백이 처리될때, 앞의 독립적인 트랜잭션은 정상적으로 수행된다`() {
        val user = User.fixture(email = userEmail)
        userRepository.save(user)

        try {
            testService.addUserPointWithPartialTx(user.id, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val foundUser = userRepository.findById(user.id).get()
        assertEquals(foundUser.point, 100)
    }
}


@Component
class TestService(
    private val userRepository: UserRepository
){
    fun addUserPointWithAllTx(userId: Long, point: Int)= transactional{
        addPoint1(userId, point)
        failAddPoint(userId, point)
    }

    fun addUserPointWithPartialTx(userId: Long, point: Int){
        val user = transactional { userRepository.findByIdOrThrow(userId) }

        addPoint1(userId, point)
        failAddPoint(userId, point)
    }

    private fun addPoint1(userId: Long, point: Int){
        transactional {
            val user = userRepository.findByIdOrThrow(userId)
            user.point += point
        }
    }
    private fun failAddPoint(userId: Long, point: Int){
        transactional {
            val user = userRepository.findByIdOrThrow(userId)
            user.point += point
            throw RuntimeException("fail")
        }
    }
}