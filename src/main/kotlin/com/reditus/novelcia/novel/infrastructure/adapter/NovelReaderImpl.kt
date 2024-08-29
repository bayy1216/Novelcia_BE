package com.reditus.novelcia.novel.infrastructure.adapter

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.novel.domain.Novel

import com.reditus.novelcia.novel.domain.port.NovelReader
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.novel.domain.QNovel.novel
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import org.springframework.stereotype.Repository

@Repository
class NovelReaderImpl(
    private val novelRepository: NovelRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : NovelReader {
    override fun getNovelById(id: Long): Novel {
        val novel = novelRepository.findByIdOrThrow(id)
        if(novel.isDeleted){
            throw NoSuchElementException()
        }
        return novel
    }

    override fun findNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel> {
        val query = jpaQueryFactory
            .select(novel).from(novel)
            .where(
                cursorWhereIdEqExpression(cursorRequest),
                novel.isDeleted.eq(false)
            )
            .orderBy(novel.createdAt.desc(), novel.id.desc())
            .limit(cursorRequest.size.toLong())
            .fetch()
        return query
    }

    override fun findNovelsByIdsIn(ids: List<Long>): List<Novel> {
        return novelRepository.findAllById(ids)
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