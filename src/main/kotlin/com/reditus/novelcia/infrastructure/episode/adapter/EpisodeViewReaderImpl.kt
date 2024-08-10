package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.episode.QEpisodeView
import com.reditus.novelcia.domain.episode.port.EpisodeViewReader
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeViewReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeViewReader {
    override fun getAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeView> {
        return jpaQueryFactory
            .selectFrom(QEpisodeView.episodeView)
            .where(
                QEpisodeView.episodeView.createdAt.between(
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
                )
            )
            .fetch()
    }
}