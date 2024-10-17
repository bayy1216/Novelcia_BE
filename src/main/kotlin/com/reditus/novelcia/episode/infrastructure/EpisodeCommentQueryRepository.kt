package com.reditus.novelcia.episode.infrastructure

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.QEpisodeComment
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeCommentQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {

    fun findAllWithEpisodeByDaysBetweenCreatedAt(
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


    fun findByEpisodeIdPagingOrderByPath(episodeId: Long, pageRequest: PageRequest): List<EpisodeComment> {
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

}