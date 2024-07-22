package com.reditus.novelcia.interfaces.episode

import com.reditus.novelcia.domain.episode.EpisodeModel
import com.reditus.novelcia.domain.episode.EpisodeQueryService
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Episodes")
@RestController
class EpisodeController(
    private val episodeQueryService: EpisodeQueryService,
) {
    @GetMapping("/api/novels/{novelId}/episodes")
    fun getEpisodesOffsetPaging(
        @PathVariable novelId: Long,
        sort: EpisodePagingSort,
        page: Int,
        size: Int,
    ): List<EpisodeModel.Meta>{
        val models = episodeQueryService.getEpisodeModelsByOffsetPaging(novelId, PageRequest.of(page, size), sort)
        return models
    }
}