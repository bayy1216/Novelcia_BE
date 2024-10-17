package com.reditus.novelcia.episode.infrastructure.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.domain.QEpisodeLike
import com.reditus.novelcia.episode.application.port.EpisodeLikeReader
import com.reditus.novelcia.episode.infrastructure.EpisodeLikeRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeLikeReaderWriterImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val episodeLikeRepository: EpisodeLikeRepository,
) : EpisodeLikeReader {


    override fun findByEpisodeIdAndUserId(episodeId: Long, userId: Long): EpisodeLike? {
        return episodeLikeRepository.findByEpisodeIdAndUserId(episodeId, userId)
    }

    override fun countByEpisodeId(episodeId: Long): Long {
        return episodeLikeRepository.countByEpisodeId(episodeId)
    }

    override fun findAllWithEpisodeByDaysBetweenCreatedAt(
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