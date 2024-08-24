package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.EpisodeLike
import com.reditus.novelcia.domain.episode.QEpisodeLike
import com.reditus.novelcia.domain.episode.port.EpisodeLikeReader
import com.reditus.novelcia.domain.episode.port.EpisodeLikeWriter
import com.reditus.novelcia.infrastructure.episode.EpisodeLikeRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class EpisodeLikeReaderWriterImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val episodeLikeRepository: EpisodeLikeRepository,
) : EpisodeLikeWriter, EpisodeLikeReader {
    override fun save(episodeLike: EpisodeLike): EpisodeLike {
        return episodeLikeRepository.save(episodeLike)
    }

    override fun deleteByEpisodeIdAndUserId(episodeId: Long, userId: Long) {
        val affected = episodeLikeRepository.deleteByEpisodeIdAndUserId(episodeId, userId)
        if (affected == 0) {
            throw IllegalArgumentException("해당 에피소드 좋아요 정보가 존재하지 않습니다.")
        }
    }

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