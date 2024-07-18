package com.reditus.novelcia.domain

@JvmInline
value class PositiveInt(
    val value: Int,
) {
    init {
        require(value >= 0) { "PositiveInt must be positive" }
    }
}