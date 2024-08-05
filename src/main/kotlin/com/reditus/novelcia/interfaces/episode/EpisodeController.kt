package com.reditus.novelcia.interfaces.episode

import com.reditus.novelcia.domain.episode.application.EpisodeModel
import com.reditus.novelcia.domain.episode.application.EpisodeQueryService
import com.reditus.novelcia.domain.episode.application.EpisodeService
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Episodes", description = "에피소드")
@RestController
class EpisodeController(
    private val episodeService: EpisodeService,
    private val episodeQueryService: EpisodeQueryService,
) {
    @Operation(summary = "에피소드 목록 조회")
    @GetMapping("/api/novels/{novelId}/episodes")
    fun getEpisodesOffsetPaging(
        @PathVariable novelId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        sort: EpisodePagingSort = EpisodePagingSort.EPISODE_NUMBER_DESC,
        page: Int = 0,
        size: Int = 20,
    ): List<EpisodeModel.Meta> {
        val models = episodeQueryService.getEpisodeModelsByOffsetPaging(
            loginUserDetails.loginUserId,
            novelId,
            PageRequest.of(page, size),
            sort,
        )
        return models
    }

    @Operation(summary = "에피소드 상세 조회")
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

    @Operation(summary = "에피소드 생성", description = "에피소드 생성 후 ID 반환")
    @ResponseStatus(HttpStatus.CREATED)
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

    @Operation(summary = "에피소드 수정 PATCH", description = "수정할 필드만 전달")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/api/novels/episodes/{episodeId}")
    fun updateEpisode(
        @PathVariable episodeId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @RequestBody req: EpisodeReq.Patch,
    ) {
        val command = req.toCommand()
        episodeService.patchEpisode(
            userId = loginUserDetails.loginUserId,
            episodeId = episodeId,
            command = command,
        )
    }

    @Operation(summary = "에피소드 삭제")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/novels/episodes/{episodeId}")
    fun deleteEpisode(
        @PathVariable episodeId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        episodeService.deleteEpisode(
            userId = loginUserDetails.loginUserId,
            episodeId = episodeId,
        )
    }

    @Operation(summary = "에피소드 좋아요")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/api/episodes/{episodeId}/like")
    fun likeEpisode(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @PathVariable episodeId: Long,
    ) {
        episodeService.likeEpisode(
            userId = loginUserDetails.loginUserId,
            episodeId = episodeId,
        )
    }

    @Operation(summary = "에피소드 좋아요 취소")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/api/episodes/{episodeId}/unlike")
    fun unlikeEpisode(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @PathVariable episodeId: Long,
    ) {
        episodeService.unlikeEpisode(
            userId = loginUserDetails.loginUserId,
            episodeId = episodeId,
        )
    }
}