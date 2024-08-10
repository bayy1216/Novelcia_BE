package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeLike
import java.time.LocalDate

interface EpisodeLikeReader {
    fun findByEpisodeIdAndUserId(episodeId: Long, userId: Long): EpisodeLike?
    fun countByEpisodeId(episodeId: Long): Long
    fun findAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeLike>
}