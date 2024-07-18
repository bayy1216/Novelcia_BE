package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.novel.NovelService
import org.springframework.web.bind.annotation.*

@RestController
class NovelController(
    private val novelService: NovelService,
) {
    @PostMapping("/api/novels")
    fun createNovel(
        @RequestBody req: NovelReq.Create,
    ) {
        novelService.registerNovel(
            loginUserId = LoginUserId(1),
            command = req.toCommand(),
        )
    }

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