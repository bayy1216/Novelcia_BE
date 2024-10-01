package com.reditus.novelcia.episode.infrastructure.adapter

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.QEpisodeComment
import com.reditus.novelcia.episode.application.port.EpisodeCommentReader
import com.reditus.novelcia.episode.application.port.EpisodeCommentWriter
import com.reditus.novelcia.episode.infrastructure.EpisodeCommentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeCommentReaderWriterImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val episodeCommentRepository: EpisodeCommentRepository,
) : EpisodeCommentReader, EpisodeCommentWriter {

    override fun findAllWithEpisodeByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeComment> {
        return jpaQueryFactory
            .selectFrom(QEpisodeComment.episodeComment)
            .innerJoin(QEpisodeComment.episodeComment.episode).fetchJoin()
            .where(
                QEpisodeComment.episodeComment.createdAt.between(
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
                )
            )
            .fetch()
    }

    override fun getById(id: Long): EpisodeComment {
        val comment =  episodeCommentRepository.findByIdOrThrow(id)
        if(comment.isDeleted){
            throw NoSuchElementException()
        }
        return comment
    }

    override fun findByEpisodeIdPagingOrderByPath(episodeId: Long, pageRequest: PageRequest): List<EpisodeComment> {
        val parentAlias = QEpisodeComment("parentAlias")
        val commentAlias = QEpisodeComment.episodeComment
        return jpaQueryFactory
            .selectFrom(QEpisodeComment.episodeComment)
            .innerJoin(QEpisodeComment.episodeComment.user).fetchJoin()
            .leftJoin(commentAlias.parent, parentAlias)
            .where(
                QEpisodeComment.episodeComment.episode.id.eq(episodeId)
            )
            .orderBy(
                CaseBuilder()
                    .`when`(commentAlias.parent.isNull)
                    .then(commentAlias.id)
                    .otherwise(parentAlias.id)
                    .asc(),
            )
            .offset(pageRequest.offset)
            .limit(pageRequest.pageSize.toLong())
            .fetch()
    }

    override fun save(comment: EpisodeComment): EpisodeComment {
        return episodeCommentRepository.save(comment)
    }

    override fun delete(comment: EpisodeComment) {
        comment.isDeleted = true
    }
}