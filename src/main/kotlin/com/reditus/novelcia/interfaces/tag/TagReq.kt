package com.reditus.novelcia.interfaces.tag

import com.reditus.novelcia.domain.novel.TagCommand

class TagReq {
    data class Create(
        val name: String,
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