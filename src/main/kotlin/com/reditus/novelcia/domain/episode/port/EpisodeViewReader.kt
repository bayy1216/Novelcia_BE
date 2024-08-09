package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeView

interface EpisodeViewReader {
    fun getAllByEpisodeIds(ids: List<Long>): List<EpisodeView>
}