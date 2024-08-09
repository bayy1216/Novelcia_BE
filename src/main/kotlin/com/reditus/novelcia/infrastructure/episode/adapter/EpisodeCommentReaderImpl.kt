package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.EpisodeComment
import com.reditus.novelcia.domain.episode.QEpisodeComment
import com.reditus.novelcia.domain.episode.port.EpisodeCommentReader
import org.springframework.stereotype.Repository

@Repository
class EpisodeCommentReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeCommentReader {
    override fun getAllByEpisodeIds(ids: List<Long>): List<EpisodeComment> {
        return jpaQueryFactory
            .selectFrom(QEpisodeComment.episodeComment)
            .where(QEpisodeComment.episodeComment.episode.id.`in`(ids))
            .fetch()
    }
}