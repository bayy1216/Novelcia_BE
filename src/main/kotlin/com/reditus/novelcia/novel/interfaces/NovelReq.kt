package com.reditus.novelcia.novel.interfaces

import com.reditus.novelcia.novel.domain.NovelCommand
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

class NovelReq {
    data class Create(
        val title: String,
        val description: String,
        @Schema(example = "https://picsum.photos/seed/T0GXvPB9C5/640/480")
        val thumbnailImageUrl: String?,
        @ArraySchema(
            schema = Schema(example = "태그1"),
        )
        val tagNames: List<String>,
        val speciesNames: List<String>,
    ) {
        fun toCommand() = NovelCommand.Create(
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            tagNames = tagNames,
            speciesNames = speciesNames,
        )
    }

    data class Update(
        val title: String,
        val description: String,
        @Schema(example = "https://picsum.photos/seed/T0GXvPB9C5/640/480")
        val thumbnailImageUrl: String?,
        @ArraySchema(
            schema = Schema(example = "태그1"),
        )
        val tagNames: List<String>,
        val speciesNames: List<String>,
    ) {
        fun toCommand() = NovelCommand.Update(
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            tagNames = tagNames,
            speciesNames = speciesNames,
        )
    }
}