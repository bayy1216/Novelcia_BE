package com.reditus.novelcia.novel.domain.application

import com.reditus.novelcia.novel.domain.Tag
import com.reditus.novelcia.global.util.TxScope

class TagModel(
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(tag: Tag): TxScope.() -> TagModel = {
            TagModel(
                name = tag.name,
                colorHexCode = tag.colorHexCode,
            )
        }
    }
}