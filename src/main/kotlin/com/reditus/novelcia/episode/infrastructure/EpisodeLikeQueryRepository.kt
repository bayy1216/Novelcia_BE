package com.reditus.novelcia.episode.infrastructure

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.domain.QEpisodeLike
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeLikeQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
)  {


    fun findAllWithEpisodeByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeLike> {
        return jpaQueryFactory
            .selectFrom(QEpisodeLike.episodeLike)
            .innerJoin(QEpisodeLike.episodeLike.episode).fetchJoin()
            .where(
                QEpisodeLike.episodeLike.createdAt.between(
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
                )
            )
            .fetch()
    }
}