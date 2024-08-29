package com.reditus.novelcia.common.infrastructure

import com.reditus.novelcia.common.domain.IdempotencyEvent
import com.reditus.novelcia.common.domain.IdempotencyEventStore
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class IdempotencyEventStoreImpl(
    private val em: EntityManager,
) : IdempotencyEventStore {
    override fun save(idempotencyKey: String) {
        em.persist(IdempotencyEvent(idempotencyKey))
        em.flush()
    }
}