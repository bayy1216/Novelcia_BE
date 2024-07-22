package com.reditus.novelcia.interfaces.episode

import com.reditus.novelcia.domain.episode.EpisodeModel
import com.reditus.novelcia.domain.episode.EpisodeQueryService
import com.reditus.novelcia.domain.episode.EpisodeService
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Episodes")
@RestController
class EpisodeController(
    private val episodeService: EpisodeService,
    private val episodeQueryService: EpisodeQueryService,
) {
    @GetMapping("/api/novels/{novelId}/episodes")
    fun getEpisodesOffsetPaging(
        @PathVariable novelId: Long,
        sort: EpisodePagingSort,
        page: Int,
        size: Int,
    ): List<EpisodeModel.Meta> {
        val models = episodeQueryService.getEpisodeModelsByOffsetPaging(novelId, PageRequest.of(page, size), sort)
        return models
    }

    @GetMapping("/api/episodes/{episodeId}")
    fun getEpisodeDetail(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @PathVariable episodeId: Long,
    ): EpisodeModel.Main {
        return episodeQueryService.getEpisodeDetail(
            episodeId = episodeId,
            userId = loginUserDetails.loginUserId,
        )
    }

    @PostMapping("/api/novels/{novelId}/episodes")
    fun createEpisode(
        @PathVariable novelId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @RequestBody req: EpisodeReq.Create,
    ): Long {
        val command = req.toCommand()
        return episodeService.createEpisode(
            userId = loginUserDetails.loginUserId,
            novelId = novelId,
            command = command,
        )
    }
}