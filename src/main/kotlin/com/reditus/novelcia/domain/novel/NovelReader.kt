package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.CursorRequest

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun getAllTags(): List<Tag>
    fun getNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel>
}