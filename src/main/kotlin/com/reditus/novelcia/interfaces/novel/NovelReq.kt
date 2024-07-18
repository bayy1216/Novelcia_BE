package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.novel.NovelCommand

class NovelReq {
    data class Create(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
    ) {
        fun toCommand() = NovelCommand.Create(
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            tagNames = tagNames,
        )
    }

    data class Update(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
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