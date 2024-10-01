package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.EpisodeView

interface EpisodeViewWriter {
    fun save(episodeView: EpisodeView): EpisodeView
}