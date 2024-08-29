package com.reditus.novelcia.episode.domain.port

import com.reditus.novelcia.episode.domain.EpisodeComment
import java.time.LocalDate

interface EpisodeCommentReader {
    fun findAllWithEpisodeByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeComment>
}