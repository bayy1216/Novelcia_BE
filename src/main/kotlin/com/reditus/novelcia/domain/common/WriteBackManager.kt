package com.reditus.novelcia.domain.common

interface WriteBackManager<T> {
    val flushIntervalMillis: Long
    val flushSize: Int
    fun save(entity: T)
    fun flush(force: Boolean)
}

