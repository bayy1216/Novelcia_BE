package com.reditus.novelcia.episode.infrastructure

import com.reditus.novelcia.episode.domain.EpisodeView
import org.springframework.data.jpa.repository.JpaRepository

interface EpisodeViewRepository: JpaRepository<EpisodeView, Long> {
}