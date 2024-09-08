package com.reditus.novelcia.episode.domain.port

import com.reditus.novelcia.episode.domain.EpisodeView

interface EpisodeViewWriter {
    fun save(episodeView: EpisodeView): EpisodeView
}