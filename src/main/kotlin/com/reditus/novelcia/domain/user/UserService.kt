package com.reditus.novelcia.domain.user

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.PositiveInt
import com.reditus.novelcia.domain.user.port.UserReader
import com.reditus.novelcia.domain.user.port.UserWriter
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
) {
    fun getUserModelById(userId: Long): UserModel = transactional {
        val user = userReader.getById(userId)
        UserModel.from(user)(this)
    }


    /**
     * 유저 포인트 충전
     * JPA의 Modifying 어노테이션을 사용하여 트랜잭션 내에서 업데이트 쿼리를 실행한다.
     * - clear을 사용하기때문에 쿼리 실행 후 영속성 컨텍스트를 초기화한다.\
     */
    fun chargePoint(userId: LoginUserId, point: PositiveInt) = transactional {
        userWriter.chargePoint(userId = userId.value, point = point)
    }
}