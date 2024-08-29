package com.reditus.novelcia.common.domain

/**
 * 유효성 검사를 통과한 양수를 나타내는 값 클래스
 * TX를 시작하기 전에 빠른 에러반환을 위해 사용한다.
 */
@JvmInline
value class PositiveInt(
    val value: Int,
) {
    init {
        require(value >= 0) { "PositiveInt must be positive" }
    }
}