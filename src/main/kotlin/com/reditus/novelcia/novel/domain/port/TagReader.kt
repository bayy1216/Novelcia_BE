package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.Tag

interface TagReader {
    fun findTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun findAllTags(): List<Tag>
}