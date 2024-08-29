package com.reditus.novelcia.episode.infrastructure.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.QEpisodeComment
import com.reditus.novelcia.episode.domain.port.EpisodeCommentReader
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeCommentReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeCommentReader {

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
}