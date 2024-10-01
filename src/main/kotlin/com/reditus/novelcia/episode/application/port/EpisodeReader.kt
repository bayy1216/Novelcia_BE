package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.application.model.EpisodeModel
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

interface EpisodeReader {
    fun getEpisodeModelsByOffsetPaging(
        userId: Long,
        novelId: Long, pageRequest: PageRequest, sort: EpisodePagingSort,
    ): List<EpisodeModel.Meta>

    fun getById(episodeId: Long): Episode
    fun getByIdWithNovel(episodeId: Long): Episode
    fun getByEpisodeNumberAndNovelIdWithNovel(novelId: Long, episodeNumber: Int): Episode

    fun findLastEpisodeNumberByNovelId(novelId: Long): Int?

    fun getReferenceById(id: Long): Episode

    fun findEpisodesDaysBetweenByCreatedAt(startDate: LocalDate, endDate: LocalDate): List<Episode>
}

enum class EpisodePagingSort {
    EPISODE_NUMBER_DESC, EPISODE_NUMBER_ASC
}