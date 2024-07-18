package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.novel.NovelQueryService
import com.reditus.novelcia.domain.novel.NovelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Novel")
@RestController
class NovelController(
    private val novelService: NovelService,
    private val novelQueryService: NovelQueryService,
) {

    @Operation(summary = "소설 cursor 조회")
    @GetMapping("/api/novels")
    fun getNovels(
        @RequestParam cursorId: Long?,
        @RequestParam size: Int = 20,
    ): List<NovelRes.Meta> {
        val models = novelQueryService.getNovelModelsByCursor(
            CursorRequest(cursorId, size)
        )
        return models.map(NovelRes.Meta::from)
    }

    @Operation(summary = "소설 생성")
    @PostMapping("/api/novels")
    fun createNovel(
        @RequestBody req: NovelReq.Create,
    ) {
        novelService.registerNovel(
            loginUserId = LoginUserId(1),
            command = req.toCommand(),
        )
    }

    @Operation(summary = "소설 수정")
    @PutMapping("/api/novels/{novelId}")
    fun updateNovel(
        @PathVariable novelId: Long,
        @RequestBody req: NovelReq.Update,
    ) {
        novelService.updateNovel(
            loginUserId = LoginUserId(1),
            novelId = novelId,
            command = req.toCommand(),
        )
    }

    @Operation(summary = "소설 삭제")
    @DeleteMapping("/api/novels/{novelId}")
    fun deleteNovel(
        @PathVariable novelId: Long,
    ) {
        novelService.deleteNovel(
            loginUserId = LoginUserId(1),
            novelId = novelId,
        )
    }
}