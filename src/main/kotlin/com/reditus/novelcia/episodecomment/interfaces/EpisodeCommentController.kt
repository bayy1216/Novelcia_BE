package com.reditus.novelcia.episodecomment.interfaces

import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.episodecomment.application.EpisodeCommentModel
import com.reditus.novelcia.episodecomment.application.EpisodeCommentService
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Episode Comments", description = "에피소드 댓글")
@RestController
class EpisodeCommentController(
    private val episodeCommentService: EpisodeCommentService
) {

    @Operation(summary = "에피소드 댓글 페이징 조회")
    @GetMapping("/api/episodes/{episodeId}/comments")
    fun getEpisodeComments(
        @PathVariable episodeId: Long,
        @ParameterObject offsetRequest: OffsetRequest,
    ) : List<EpisodeCommentModel.Main> {
        return episodeCommentService.getEpisodeCommentModelsByPaging(episodeId, offsetRequest.toPageRequest())
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "에피소드 댓글 등록 후 ID 반환")
    @PostMapping("/api/episodes/{episodeId}/comments")
    fun createEpisodeComment(
        @PathVariable episodeId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @RequestBody req: EpisodeCommentReq.Create,
    ): Long {
        val command = req.toCommand()
        return episodeCommentService.createEpisodeComment(episodeId, command, loginUserDetails.loginUserId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "에피소드 댓글 수정")
    @PutMapping("/api/episodes/comments/{commentId}")
    fun updateEpisodeComment(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @RequestBody req: EpisodeCommentReq.Update,
    ) {
        val command = req.toCommand()
        episodeCommentService.updateEpisodeComment(commentId, command, loginUserDetails.loginUserId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "에피소드 댓글 삭제")
    @DeleteMapping("/api/episodes/comments/{commentId}")
    fun deleteEpisodeComment(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        episodeCommentService.deleteEpisodeComment(commentId, loginUserDetails.loginUserId)
    }


}