package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.episode.QEpisodeView
import com.reditus.novelcia.domain.episode.port.EpisodeViewReader
import org.springframework.stereotype.Repository

@Repository
class EpisodeViewReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : EpisodeViewReader {
    override fun getAllByEpisodeIds(ids: List<Long>): List<EpisodeView> {
        return jpaQueryFactory
            .selectFrom(QEpisodeView.episodeView)
            .where(QEpisodeView.episodeView.episode.id.`in`(ids))
            .fetch()
    }
}