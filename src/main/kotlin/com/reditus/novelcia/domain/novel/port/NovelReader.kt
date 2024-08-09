package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.Tag

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun getNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel>

    fun getNovelsByIdsIn(ids: List<Long>): List<Novel>
}