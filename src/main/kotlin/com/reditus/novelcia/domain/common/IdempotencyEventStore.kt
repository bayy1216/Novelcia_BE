package com.reditus.novelcia.domain.common

interface IdempotencyEventStore {
    fun save(idempotencyKey: String)
}