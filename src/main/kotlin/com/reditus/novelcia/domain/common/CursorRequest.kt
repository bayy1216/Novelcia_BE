package com.reditus.novelcia.domain.common

class CursorRequest(
    val cursorId: Long? = null,
    val size: Int = 20,
) {
    init {
        require(size in 1..100) { "size는 1~100 사이여야 합니다." }
    }
}