package com.reditus.novelcia.novel.interfaces

import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.novel.domain.application.NovelQueryService
import com.reditus.novelcia.novel.domain.application.NovelService
import com.reditus.novelcia.global.security.LoginUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Novel", description = "소설")
@RestController
class NovelController(
    private val novelService: NovelService,
    private val novelQueryService: NovelQueryService,
) {

    @Operation(summary = "소설 생성일 최근 날짜순 cursor 조회")
    @GetMapping("/api/novels")
    fun getNovels(
        @RequestParam cursorId: Long?,
        @RequestParam size: Int = 20,
    ): List<NovelRes.Meta> {
        val models = novelQueryService.getNovelModelsByCursor(
            CursorRequest(cursorId, size)
        )
        return models.map(NovelRes.Meta.Companion::from)
    }

    @Operation(summary = "소설 최근 N일 랭킹 조회")
    @GetMapping("/api/novels/ranking")
    fun getNovelRanking(
        @RequestParam days: Int = 1,
        @RequestParam size: Int = 20,
        @RequestParam page: Int = 0,
    ): List<NovelRes.Meta> {
        val models = novelQueryService.getNovelModelsByRanking(
            days = days,
            size = size,
            page = page,
        )
        return models.map(NovelRes.Meta.Companion::from)
    }

    @Operation(summary = "소설 생성", description = "소설 생성 후 ID 반환")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/novels")
    fun createNovel(
        @RequestBody req: NovelReq.Create,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) :Long {
        return novelService.registerNovel(
            loginUserId = loginUserDetails.loginUserId,
            command = req.toCommand(),
        )
    }

    @Operation(summary = "소설 수정")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/api/novels/{novelId}")
    fun updateNovel(
        @PathVariable novelId: Long,
        @RequestBody req: NovelReq.Update,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        novelService.updateNovel(
            loginUserId = loginUserDetails.loginUserId,
            novelId = novelId,
            command = req.toCommand(),
        )
    }

    @Operation(summary = "소설 삭제")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/novels/{novelId}")
    fun deleteNovel(
        @PathVariable novelId: Long,
        @AuthenticationPrincipal loginUserDetails: LoginUserDetails,
    ) {
        novelService.deleteNovel(
            loginUserId = loginUserDetails.loginUserId,
            novelId = novelId,
        )
    }
}