package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.application.EpisodeModel
import org.springframework.data.domain.PageRequest

interface EpisodeReader {
    fun getEpisodeModelsByOffsetPaging(
        userId: Long,
        novelId: Long, pageRequest: PageRequest, sort: EpisodePagingSort,
    ): List<EpisodeModel.Meta>

    fun getById(episodeId: Long): Episode
    fun getByIdWithNovel(episodeId: Long): Episode
    fun getByEpisodeNumberAndNovelIdWithNovel(novelId: Long, episodeNumber: Int): Episode

    fun getLastEpisodeNumberByNovelId(novelId: Long): Int?

    fun getReferenceById(id: Long): Episode
}

enum class EpisodePagingSort {
    EPISODE_NUMBER_DESC, EPISODE_NUMBER_ASC
}