package com.reditus.novelcia.domain.common

interface WriteBackManager<T> {
    val flushSize: Int
    fun save(entity: T)
    fun flush()
}

