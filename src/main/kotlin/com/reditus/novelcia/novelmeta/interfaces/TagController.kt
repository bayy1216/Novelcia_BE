package com.reditus.novelcia.novelmeta.interfaces

import com.reditus.novelcia.common.domain.UpsertResult
import com.reditus.novelcia.novelmeta.application.TagModel
import com.reditus.novelcia.novelmeta.application.TagService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Tag", description = "태그")
@RestController
class TagController(
    private val tagService: TagService,
) {
    @Operation(summary = "[PUBLIC] 태그 목록 전체 조회")
    @GetMapping("/api/tags")
    fun getAllTags(): List<TagModel> = tagService.getAllTags()

    @Operation(summary = "[ADMIN] 태그 생성/수정 Upsert")
    @PostMapping("/api/admin/tags")
    fun upsertTags(
        @RequestBody req: TagReq.CreateTags,
    ) : UpsertResult<String> {
        val upsertResult = tagService.upsertTags(req.toCommands())
        return upsertResult
    }

}