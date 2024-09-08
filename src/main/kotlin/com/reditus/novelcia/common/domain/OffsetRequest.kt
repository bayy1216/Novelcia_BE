package com.reditus.novelcia.common.domain

import org.springframework.data.domain.PageRequest

class OffsetRequest(
    val page: Int = 0,
    val size: Int = 20,
) {
    init {
        require(size in 1..100) { "size는 1~100 사이여야 합니다." }
    }

    fun toPageRequest() = PageRequest.of(page, size)
}