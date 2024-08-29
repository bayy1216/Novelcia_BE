package com.reditus.novelcia.common.domain

interface WriteBackManager<T> {
    val flushSize: Int
    fun save(entity: T)
    fun flush()
}

