package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.global.util.TxContext

class TagModel(
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(tag: Tag): TxContext.() -> TagModel = {
            TagModel(
                name = tag.name,
                colorHexCode = tag.colorHexCode,
            )
        }
    }
}