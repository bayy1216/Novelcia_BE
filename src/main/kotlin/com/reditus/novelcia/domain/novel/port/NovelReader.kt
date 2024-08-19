package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.common.CursorRequest
import com.reditus.novelcia.domain.novel.Novel

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun findNovelsByCursorOrderByCreatedAt(cursorRequest: CursorRequest): List<Novel>

    fun findNovelsByIdsIn(ids: List<Long>): List<Novel>
}