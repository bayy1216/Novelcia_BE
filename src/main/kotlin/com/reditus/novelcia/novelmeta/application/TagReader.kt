package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Tag

interface TagReader {
    fun findTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun findAllTags(): List<Tag>
}