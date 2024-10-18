package com.reditus.novelcia.novel.infrastructure

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.novel.domain.Novel

import com.reditus.novelcia.novel.domain.QNovel.novel
import org.springframework.stereotype.Repository

@Repository
class NovelQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
)  {

    fun findNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel> {
        val query = jpaQueryFactory
            .select(novel).from(novel)
            .where(
                cursorWhereIdEqExpression(cursorRequest),
                novel.deletedAt.isNull
            )
            .orderBy(novel.createdAt.desc(), novel.id.desc())
            .limit(cursorRequest.size.toLong())
            .fetch()
        return query
    }


    private fun cursorWhereIdEqExpression(cursorRequest: CursorRequest): BooleanExpression? {
        if(cursorRequest.cursorId == null){
            return null
        }
        return novel.createdAt.lt(
            JPAExpressions.select(novel.createdAt)
                .from(novel)
                .where(novel.id.eq(cursorRequest.cursorId))
        ).or(
            novel.createdAt.eq(
                JPAExpressions.select(novel.createdAt)
                    .from(novel)
                    .where(novel.id.eq(cursorRequest.cursorId))
            ).and(novel.id.lt(cursorRequest.cursorId))
        )
    }
}