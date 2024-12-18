package com.reditus.novelcia.domain.user

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.user.infrastructure.UserRepository
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.application.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.Executors

@Tag("medium")
@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
){
    @AfterEach
    fun cleanUp() {
        userRepository.deleteAllInBatch()
    }

    @Test
    @Transactional
    fun `포인트 충전이 정상적으로 작동한다`() {
        val user = userRepository.save(User.fixture(point = 10))

        userService.chargePoint(LoginUserId(user.id), PositiveInt(10), "idempotencyKey")


        val user1 = userRepository.findByIdOrThrow(user.id)

        assertEquals(20, user1.point,{ "포인트가 정상적으로 충전되었다" })
    }

    @Test
    @Transactional
    fun `포인트 충전이 멱등성을 보장한다`() {
        val user = userRepository.save(User.fixture(point = 10))

        userService.chargePoint(LoginUserId(user.id), PositiveInt(10), "idempotencyKey")

        assertThrows<RuntimeException>("멱등키가 중복시 에러를 던진다") {
            userService.chargePoint(LoginUserId(user.id), PositiveInt(10), "idempotencyKey")
        }
    }

    @Test
    @Transactional
    fun `멱등성 키가 중복되어도 포인트 충전이 정상적으로 작동한다`() {
        val user = userRepository.save(User.fixture(point = 10))

        userService.chargePoint(LoginUserId(user.id), PositiveInt(10), "idempotencyKey")


        try {
            userService.chargePoint(LoginUserId(user.id), PositiveInt(10), "idempotencyKey")
        }catch (e: Exception) {
            println(e)
        }


        val user1 = userRepository.findByIdOrThrow(user.id)

        assertEquals(20,user1.point, { "멱등키 중복시 한번만 충전되었다" })

    }

    @Test
    fun `동시성 문제가 있어도 정상 작동한다`(){
        val user =  userRepository.save(User.fixture(point = 10))

        val executors = Executors.newFixedThreadPool(10)

        repeat(10) {
            val key = "idempotencyKey$it"
            executors.submit {
                userService.chargePoint(LoginUserId(user.id), PositiveInt(10), key)

            }
        }

        executors.shutdown()
        executors.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)

        println("USER POINT IS ${user.point}")



        val user1 =  userRepository.findByIdOrThrow(user.id)

        println("USER1 POINT IS ${user1.point}")
        assertEquals(110, user1.point, {"동시성에 문제가 없어야 한다"})
    }
}