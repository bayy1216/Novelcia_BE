package com.reditus.novelcia.infrastructure.novel

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelReader
import com.reditus.novelcia.domain.novel.QNovel.novel
import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Repository

@Repository
class NovelReaderImpl(
    private val novelRepository: NovelRepository,
    private val tagRepository: TagRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : NovelReader {
    override fun getNovelById(id: Long): Novel {
        return novelRepository.findByIdOrThrow(id)
    }

    override fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag> {
        return tagRepository.findAllByNameIn(tagNames)
    }

    override fun getAllTags(): List<Tag> {
        return tagRepository.findAll()
    }

    override fun getNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel> {
        val query = jpaQueryFactory
            .select(novel).from(novel)
            .where(
                cursorWhereExpression(cursorRequest)
            )
            .orderBy(novel.createdAt.desc(), novel.id.desc())
            .limit(cursorRequest.size.toLong())
            .fetch()
        return query
    }

    private fun cursorWhereExpression(cursorRequest: CursorRequest): BooleanExpression? {
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