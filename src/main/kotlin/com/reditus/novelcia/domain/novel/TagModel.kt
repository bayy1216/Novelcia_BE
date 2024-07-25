package com.reditus.novelcia.domain.novel

class TagModel(
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(tag: Tag): TagModel = TagModel(
            name = tag.name,
            colorHexCode = tag.colorHexCode,
        )
    }
}