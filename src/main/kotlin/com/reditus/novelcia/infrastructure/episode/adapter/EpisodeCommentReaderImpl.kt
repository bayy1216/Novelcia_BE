package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.EpisodeComment
import com.reditus.novelcia.domain.episode.QEpisodeComment
import com.reditus.novelcia.domain.episode.port.EpisodeCommentReader
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeCommentReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeCommentReader {

    override fun getAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeComment> {
        return jpaQueryFactory
            .selectFrom(QEpisodeComment.episodeComment)
            .where(
                QEpisodeComment.episodeComment.createdAt.between(
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
                )
            )
            .fetch()
    }
}