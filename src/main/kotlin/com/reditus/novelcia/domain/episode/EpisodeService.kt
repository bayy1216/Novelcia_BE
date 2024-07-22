package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.episode.port.EpisodeReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EpisodeService(
    private val episodeReader: EpisodeReader,
) {

    @Transactional
    fun createEpisode() {

    }

    @Transactional
    fun updateEpisode() {

    }

    @Transactional
    fun deleteEpisode() {

    }
}