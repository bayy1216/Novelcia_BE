package com.reditus.novelcia.interfaces.tag

import com.reditus.novelcia.domain.novel.TagModel
import com.reditus.novelcia.domain.novel.TagService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(
    private val tagService: TagService,
) {
    @GetMapping("/api/tags")
    fun getAllTags(): List<TagModel> = tagService.getAllTags()

    @PostMapping("/api/tags")
    fun upsertTags(
        @RequestBody req: TagReq.CreateTags,
    ) {
        tagService.upsertTags(req.toCommands())
    }

}