package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.EpisodeView
import java.time.LocalDate

interface EpisodeViewReader {
    fun findAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeView>
}