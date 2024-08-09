package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeLike

interface EpisodeLikeReader {
    fun findByEpisodeIdAndUserId(episodeId: Long, userId: Long): EpisodeLike?
    fun countByEpisodeId(episodeId: Long): Long
    fun findAllByEpisodeIds(episodeIds: List<Long>): List<EpisodeLike>
}