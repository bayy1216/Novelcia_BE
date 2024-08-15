package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.usecase.NovelScoringUseCase
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
    private val novelScoringUseCase: NovelScoringUseCase,
) {

    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> = readOnly {
        val novels = novelReader.findNovelsByCursorOrderByCreatedAt(cursorRequest)
        return@readOnly novels.map { NovelModel.Main.from(it)(this) }
    }

    fun getNovelModelsByRanking(days: Int, size: Int, page: Int): List<NovelModel.Main> = readOnly {
        val scoresByOrder = novelScoringUseCase(days = days)

        val novelIds = scoresByOrder.map { it.novelId }.subList(
            page * size,
            minOf((page + 1) * size, scoresByOrder.size)
        )
        val novels = novelReader.findNovelsByIdsIn(novelIds)


        return@readOnly novels.map { NovelModel.Main.from(it)() }
    }



}

