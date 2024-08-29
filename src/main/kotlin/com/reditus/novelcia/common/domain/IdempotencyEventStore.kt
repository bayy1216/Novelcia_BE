package com.reditus.novelcia.common.domain

interface IdempotencyEventStore {
    fun save(idempotencyKey: String)
}