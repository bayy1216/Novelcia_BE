package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Tag

interface TagReader {
    fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun getAllTags(): List<Tag>
}