package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.Tag

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag>

    fun getAllTags(): List<Tag>
    fun getNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel>
}