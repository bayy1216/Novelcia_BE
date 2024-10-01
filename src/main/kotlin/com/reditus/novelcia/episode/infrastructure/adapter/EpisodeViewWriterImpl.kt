package com.reditus.novelcia.episode.infrastructure.adapter

import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.episode.application.port.EpisodeViewWriter
import com.reditus.novelcia.episode.infrastructure.EpisodeViewRepository
import org.springframework.stereotype.Repository

@Repository
class EpisodeViewWriterImpl (
    private val episodeViewRepository: EpisodeViewRepository,
) : EpisodeViewWriter {
    override fun save(episodeView: EpisodeView): EpisodeView {
        return episodeViewRepository.save(episodeView)
    }
}