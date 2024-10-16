package com.reditus.novelcia.novelfavorite.interfaces

import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.common.domain.OffsetResponse
import com.reditus.novelcia.novelfavorite.application.NovelFavoriteService
import com.reditus.novelcia.novel.application.model.NovelModel
import com.reditus.novelcia.global.security.LoginUserDetails
import com.reditus.novelcia.novelfavorite.application.NovelFavoriteModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "NovelFavorite", description = "소설 선호작")
@RestController
class NovelFavoriteController(
    private val novelFavoriteService: NovelFavoriteService,
) {

    @Operation(summary = "소설 선호작 목록 페이징 조회")
    @GetMapping("/api/novels/favorite")
    fun getFavoriteNovels(
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
        @ParameterObject offsetRequest: OffsetRequest,
    ) : OffsetResponse<NovelFavoriteModel.UserFavorite> {
        return novelFavoriteService.getFavoriteNovels(
            loginUserDetails.loginUserId,
            offsetRequest
        )
    }

    @Operation(summary = "소설 선호작 추가")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/novels/{novelId}/favorite")
    fun addFavoriteNovel(
        @PathVariable novelId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        novelFavoriteService.addFavoriteNovel(
            loginUserDetails.loginUserId,
            novelId
        )
    }

    @Operation(summary = "소설 선호작 삭제")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/novels/{novelId}/favorite")
    fun deleteFavoriteNovel(
        @PathVariable novelId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        novelFavoriteService.deleteFavoriteNovel(
            loginUserDetails.loginUserId,
            novelId
        )
    }
}