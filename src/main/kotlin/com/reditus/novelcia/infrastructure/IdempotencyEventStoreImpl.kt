package com.reditus.novelcia.infrastructure

import com.reditus.novelcia.domain.common.IdempotencyEvent
import com.reditus.novelcia.domain.common.IdempotencyEventStore
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