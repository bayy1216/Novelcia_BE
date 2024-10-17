package com.reditus.novelcia.user.application

import com.reditus.novelcia.common.domain.IdempotencyEventStore
import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val idempotencyEventStore: IdempotencyEventStore,
) {
    fun getUserModelById(userId: Long): UserModel = transactional {
        val user = userRepository.findByIdOrThrow(userId)
        UserModel.from(user)(this)
    }


    /**
     * 유저 포인트 충전
     * JPA의 Modifying 어노테이션을 사용하여 트랜잭션 내에서 업데이트 쿼리를 실행한다.
     * - clear을 사용하기때문에 쿼리 실행 후 영속성 컨텍스트를 초기화한다.\
     */
    fun chargePoint(
        userId: LoginUserId,
        point: PositiveInt, idempotencyKey: String,
    ) = transactional {
        val key = idempotencyKey.generateIdempotencyKey(userId.value)
        try{
            idempotencyEventStore.save(key) // 멱등성 체크, 삽입 실패시 FAIL early return
        }catch (e: DataIntegrityViolationException){
            throw IllegalStateException("이미 처리된 요청입니다.")
        }


        userRepository.chargePoint(userId = userId.value, point = point.value)
    }
}

fun String.generateIdempotencyKey(userId: Long): String = "$userId:$this"