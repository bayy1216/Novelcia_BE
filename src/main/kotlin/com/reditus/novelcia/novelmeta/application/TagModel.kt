package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Tag
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.novel.domain.NovelMeta

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

        fun from(tagData: NovelMeta.TagData): TxScope.() -> TagModel = {
            TagModel(
                name = tagData.name,
                colorHexCode = tagData.colorHexCode,
            )
        }
    }
}