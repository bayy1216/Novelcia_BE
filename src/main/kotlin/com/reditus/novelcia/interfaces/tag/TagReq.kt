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
        fun toCommands(): List<TagCommand.Create> = tags.map {
            TagCommand.Create(
                name = it.name,
                colorHexCode = it.colorHexCode,
            )
        }

    }
}