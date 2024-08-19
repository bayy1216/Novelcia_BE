package com.reditus.novelcia.domain.common

/**
 * 유효성 검사를 통과한 사용자 ID를 나타내는 값 클래스
 */
@JvmInline
value class LoginUserId(
    val value: Long,
)