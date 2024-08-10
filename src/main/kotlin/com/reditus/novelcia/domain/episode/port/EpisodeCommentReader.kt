package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeComment
import java.time.LocalDate

interface EpisodeCommentReader {
    fun findAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeComment>
}