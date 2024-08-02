package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
) {

    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> = readOnly {
        val novels = novelReader.getNovelsByCursorOrderByCreatedAt(cursorRequest)
        return@readOnly novels.map { NovelModel.Main.from(it)(this) }
    }
}