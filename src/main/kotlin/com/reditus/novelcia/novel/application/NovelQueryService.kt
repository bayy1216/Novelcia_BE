package com.reditus.novelcia.novel.application

import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.novel.application.model.NovelModel
import com.reditus.novelcia.novel.application.usecase.NovelIdAndScore
import com.reditus.novelcia.novel.domain.NovelRankingSearchDays
import com.reditus.novelcia.novel.infrastructure.NovelQueryRepository
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.novel.infrastructure.RedisNovelRankingCacheStore
import org.springframework.stereotype.Service

@Service
class NovelQueryService(
    private val novelQueryRepository: NovelQueryRepository,
    private val novelRepository: NovelRepository,
    private val novelRankingCacheStore: RedisNovelRankingCacheStore,
) {

    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> = readOnly {
        val novels = novelQueryRepository.findNovelsByCursorOrderByCreatedAt(cursorRequest)
        return@readOnly novels.map { NovelModel.Main.from(it)(this) }
    }


    fun getNovelModelsByRanking(days: NovelRankingSearchDays, size: Int, page: Int): List<NovelModel.Main> {

        val cache: List<NovelIdAndScore>? =
            novelRankingCacheStore.getNovelIdRankingByPage(days = days.value, size = size, page = page)

        if (cache != null) { // 캐시가 존재하면 캐시를 반환 early return
            return readOnly {
                val novels = novelRepository.findAllById(cache.map { it.novelId })
                return@readOnly novels.map { NovelModel.Main.from(it)() }
            }
        }

        return emptyList()
    }


}

