package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.port.NovelReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
) {
    @Transactional(readOnly = true)
    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> {
        return novelReader.getNovelsByCursorOrderByCreatedAt(cursorRequest).map { NovelModel.Main.from(it) }
    }
}