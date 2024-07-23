package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.domain.episode.EpisodeView
import org.springframework.data.jpa.repository.JpaRepository

interface EpisodeViewRepository: JpaRepository<EpisodeView, Long> {
}