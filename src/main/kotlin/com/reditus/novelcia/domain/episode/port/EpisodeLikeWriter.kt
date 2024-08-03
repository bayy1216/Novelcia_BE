package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeLike

interface EpisodeLikeWriter {
    fun save(episodeLike: EpisodeLike): EpisodeLike
    fun deleteByEpisodeIdAndUserId(episodeId: Long, userId: Long)
}