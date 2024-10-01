package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.EpisodeComment
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

interface EpisodeCommentReader {
    fun findAllWithEpisodeByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeComment>

    fun getById(id: Long): EpisodeComment

    fun findByEpisodeIdPagingOrderByPath(
        episodeId: Long,
        pageRequest: PageRequest,
    ): List<EpisodeComment>
}