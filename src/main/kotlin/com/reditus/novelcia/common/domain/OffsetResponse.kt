package com.reditus.novelcia.common.domain

class OffsetResponse<T>(
    val data: List<T>,
    val totalElements: Long,
)