package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Tag

interface TagReader {
    fun findTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun findAllTags(): List<Tag>
}