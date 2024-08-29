package com.reditus.novelcia.common.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class IdempotencyEvent(
    @Id
    val id: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
}