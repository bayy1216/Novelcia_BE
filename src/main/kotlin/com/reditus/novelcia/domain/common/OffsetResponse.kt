package com.reditus.novelcia.domain.common

class OffsetResponse<T>(
    val data: List<T>,
    val totalElements: Long,
)