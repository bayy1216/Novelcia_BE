package com.reditus.novelcia.domain

class OffsetResponse<T>(
    val data: List<T>,
    val totalElements: Int,
)