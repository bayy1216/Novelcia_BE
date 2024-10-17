package com.reditus.novelcia.episode.infrastructure

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.episode.domain.QEpisodeView
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeViewQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
)  {
    fun findAllByDaysBetweenCreatedAt(
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