package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeModel
import org.springframework.data.domain.PageRequest

interface EpisodeReader {
    fun getEpisodeModelsByOffsetPaging(novelId: Long, pageRequest: PageRequest, sort: EpisodePagingSort): List<EpisodeModel.Meta>
    fun getById(episodeId: Long): Episode
}

enum class EpisodePagingSort {
    CREATED_AT_DESC, CREATED_AT_ASC
}