package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.novel.NovelCommand
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

class NovelReq {
    @Schema(name = "NovelReq.Create")
    data class Create(
        val title: String,
        val description: String,
        @Schema(example = "https://picsum.photos/seed/T0GXvPB9C5/640/480")
        val thumbnailImageUrl: String?,
        @ArraySchema(
            schema = Schema(example = "태그1"),
        )
        val tagNames: List<String>,
    ) {
        fun toCommand() = NovelCommand.Create(
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            tagNames = tagNames,
        )
    }

    @Schema(name = "NovelReq.Update")
    data class Update(
        val title: String,
        val description: String,
        @Schema(example = "https://picsum.photos/seed/T0GXvPB9C5/640/480")
        val thumbnailImageUrl: String?,
        @ArraySchema(
            schema = Schema(example = "태그1"),
        )
        val tagNames: List<String>,
    ) {
        fun toCommand() = NovelCommand.Update(
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            tagNames = tagNames,
        )
    }
}