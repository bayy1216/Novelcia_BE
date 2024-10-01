package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.EpisodeLike
import java.time.LocalDate

interface EpisodeLikeReader {
    fun findByEpisodeIdAndUserId(episodeId: Long, userId: Long): EpisodeLike?
    fun countByEpisodeId(episodeId: Long): Long
    fun findAllWithEpisodeByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeLike>
}