package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeComment

interface EpisodeCommentReader {
    fun getAllByEpisodeIds(ids: List<Long>): List<EpisodeComment>
}