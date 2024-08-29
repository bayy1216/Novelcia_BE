package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.novel.domain.Novel

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun findNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel>

    fun findNovelsByIdsIn(ids: List<Long>): List<Novel>
}