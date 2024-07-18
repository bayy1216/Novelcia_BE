package com.reditus.novelcia.interfaces.tag

import com.reditus.novelcia.domain.novel.TagCommand
import io.swagger.v3.oas.annotations.media.Schema

class TagReq {
    @Schema(name = "TagReq.Create")
    data class Create(
        val name: String,
        @Schema(example = "#FFFFFF")
        val colorHexCode: String,
    )

    @Schema(name = "TagReq.CreateTags")
    data class CreateTags(
        val tags: List<Create>,
    ) {
        fun toCommands(): List<TagCommand.Upsert> = tags.map {
            TagCommand.Upsert(
                name = it.name,
                colorHexCode = it.colorHexCode,
            )
        }

    }
}