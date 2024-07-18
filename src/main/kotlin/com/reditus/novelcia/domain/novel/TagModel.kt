package com.reditus.novelcia.domain.novel

class TagModel(
    val id: Long,
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(tag: Tag): TagModel = TagModel(
            id = tag.id,
            name = tag.name,
            colorHexCode = tag.colorHexCode,
        )
    }
}