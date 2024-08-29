package com.reditus.novelcia.episode.domain.port

import com.reditus.novelcia.episode.domain.EpisodeLike

interface EpisodeLikeWriter {
    fun save(episodeLike: EpisodeLike): EpisodeLike
    fun deleteByEpisodeIdAndUserId(episodeId: Long, userId: Long)
}