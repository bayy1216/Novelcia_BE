package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeView
import java.time.LocalDate

interface EpisodeViewReader {
    fun findAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeView>
}