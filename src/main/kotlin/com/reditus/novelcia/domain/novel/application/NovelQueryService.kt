package com.reditus.novelcia.domain.novel.application

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
        val novels = novelReader.getNovelsByCursorOrderByCreatedAt(cursorRequest)
        return novels.map(NovelModel.Main::from)
    }
}