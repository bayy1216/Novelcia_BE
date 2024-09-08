package com.reditus.novelcia.episode.infrastructure.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.QEpisodeComment
import com.reditus.novelcia.episode.domain.port.EpisodeCommentReader
import com.reditus.novelcia.episode.domain.port.EpisodeCommentWriter
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeCommentReaderWriterImpl(
    private val jpaQueryFactory: JPAQueryFactory,
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
}