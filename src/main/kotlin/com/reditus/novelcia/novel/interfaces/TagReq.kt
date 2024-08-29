package com.reditus.novelcia.novel.interfaces

import com.reditus.novelcia.novel.domain.TagCommand
import io.swagger.v3.oas.annotations.media.Schema

class TagReq {
    data class Create(
        @Schema(example = "태그1")
        val name: String,
        @Schema(example = "#FFFFFF")
        val colorHexCode: String,
    )

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