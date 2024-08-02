package com.reditus.novelcia.global.util

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 트랜잭션을 다루기 위한 인터페이스
 *
 * [TxScope]를 수신객체로 받도록하여 트랜잭션과 관련된 작업을 수행하는 제약을 가하도록 한다.
 *
 * `Model Layer`에서 `OSIV`의 위험을 원천봉쇄할 수 있다.
 * @example com.reditus.novelcia.domain.user.UserModel.from
 */
interface TxManager {
    fun <T> transactional(block: TxScope.() -> T): T
    fun <T> readOnly(block: TxScope.() -> T): T
    fun <T> newTransaction(block: TxScope.() -> T): T
    fun <T> newReadOnlyTransaction(block: TxScope.() -> T): T
}

/**
 * 트랜잭션과 관련된 작업을 수행하는 메소드에 대한 제약을 가하기 위한 마커 인터페이스
 *
 * 수신객체를 사용하여 제약을 가한다.
 *
 * @see Effective_Java__Item_41: Use marker interfaces to define types
 */
interface TxScope


/**
 * 스프링에서 제공하는 @Transactional **AOP**를 이용한 TxManager 구현체
 * [TxManager]를 구현하고, [TxScope] 인터페이스를 상속하여 트랜잭션을 사용할 수 있도록 한다.
 *
 * 해당 클래스를 주입받아 호출하면, private 메소드에서도 트랜잭션을 사용할 수 있다.
 *
 * [Tx]클래스를 만들고 해당 클래스의 `companion object`를 사용하여
 * 편하게 [SpringTxManager]를 사용할 수 있도록 한다.
 */
@Component
class SpringTxManager : TxManager, TxScope {
    @Transactional
    override fun <T> transactional(block: TxScope.() -> T): T {
        return block()
    }

    @Transactional(readOnly = true)
    override fun <T> readOnly(block: TxScope.() -> T): T {
        return block()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun <T> newTransaction(block: TxScope.() -> T): T {
        return block()
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    override fun <T> newReadOnlyTransaction(block: TxScope.() -> T): T {
        return block()
    }
}

/**
 * [TxManager]를 사용하기 위한 클래스
 *
 * 기본적인 코틀린의 object로는 스프링의 빈을 주입받을 수 없기 때문에
 * 래퍼 클래스를 만들어 사용한다.
 */
@Component
class Tx(
    private val springTxManager: SpringTxManager,
) {
    init {
        _txManager = springTxManager
    }

    /**
     * `lateinit var`는 런타임에 수정될 가능성이 있는 변수이므로
     * `by lazy`를 사용하여 val 객체를 노출시킨다.
     */
    companion object {
        private lateinit var _txManager: TxManager
        val txManager: TxManager by lazy { _txManager }
    }
}

fun <T> transactional(block: TxScope.() -> T): T {
    return Tx.txManager.transactional(block)
}

fun <T> readOnly(block: TxScope.() -> T): T {
    return Tx.txManager.readOnly(block)
}

fun <T> newTransaction(block: TxScope.() -> T): T {
    return Tx.txManager.newTransaction(block)
}

fun <T> newReadOnlyTransaction(block: TxScope.() -> T): T {
    return Tx.txManager.newReadOnlyTransaction(block)
}

