package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Tag
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