package com.reditus.novelcia.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class IdempotencyEvent(
    @Id
    val id: String,
) {
}