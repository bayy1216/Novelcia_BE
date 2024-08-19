package com.reditus.novelcia.domain.common

/**
 * Upsert 결과를 클라이언트에게 반환하기 위한 DTO 클래스
 * (삽입 카운트, 삽입된 아이디 목록, 업데이트 카운트)로 구성
 */
class UpsertResult<T>(
    val insertedCount: Int,
    val insertedIds: List<T>,
    val updatedCount: Int,
){
    operator fun component1() = insertedCount
    operator fun component2() = insertedIds
    operator fun component3() = updatedCount
}