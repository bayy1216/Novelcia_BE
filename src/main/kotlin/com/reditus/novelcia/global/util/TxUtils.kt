package com.reditus.novelcia.global.util

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 트랜잭션을 다루기 위한 인터페이스
 *
 * `TxContext`를 수신객체로 받도록하여 트랜잭션과 관련된 작업을 수행하는 제약을 가하도록 한다.
 *
 * `Model Layer`에서 `OSIV`의 위험을 원천봉쇄할 수 있다.
 * @example com.reditus.novelcia.domain.user.UserModel.from
 */
interface TxContext {
    fun <T> transactional(block: TxContext.() -> T): T
    fun <T> readOnly(block: TxContext.() -> T): T
    fun <T> newTransaction(block: TxContext.() -> T): T
    fun <T> newReadOnlyTransaction(block: TxContext.() -> T): T
}


/**
 * 스프링에서 제공하는 @Transactional **AOP**를 이용한 TxContext 구현체
 *
 * 해당 클래스를 주입받아 호출하면, private 메소드에서도 트랜잭션을 사용할 수 있다.
 *
 * Tx클래스를 만들고 해당 클래스의 `companion object`를 사용하여
 * 편하게 `SpringTxContext`를 사용할 수 있도록 한다.
 */
@Component
class SpringTxContextUseCase : TxContext {
    @Transactional
    override fun <T> transactional(block: TxContext.() -> T): T {
        return block()
    }

    @Transactional(readOnly = true)
    override fun <T> readOnly(block: TxContext.() -> T): T {
        return block()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun <T> newTransaction(block: TxContext.() -> T): T {
        return block()
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    override fun <T> newReadOnlyTransaction(block: TxContext.() -> T): T {
        return block()
    }
}


@Component
class Tx(
    private val springTxContextUseCase: SpringTxContextUseCase,
) {
    init {
        _txContext = springTxContextUseCase
    }

    companion object {
        private lateinit var _txContext: TxContext
        val txContext : TxContext by lazy { _txContext }
    }
}
fun <T> transactional(block: TxContext.() -> T): T {
    return Tx.txContext.transactional(block)
}

fun <T> readOnly(block: TxContext.() -> T): T {
    return Tx.txContext.readOnly(block)
}

fun <T> newTransaction(block: TxContext.() -> T): T {
    return Tx.txContext.newTransaction(block)
}

fun <T> newReadOnlyTransaction(block: TxContext.() -> T): T {
    return Tx.txContext.newReadOnlyTransaction(block)
}

