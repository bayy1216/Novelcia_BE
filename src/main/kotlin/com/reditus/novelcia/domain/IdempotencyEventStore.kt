package com.reditus.novelcia.domain

interface IdempotencyEventStore {
    fun save(idempotencyKey: String)
}