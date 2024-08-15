package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.port.NovelRankingCacheStore
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.usecase.NovelAndScore
import com.reditus.novelcia.domain.novel.usecase.NovelScoringUseCase
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
    private val novelScoringUseCase: NovelScoringUseCase,
    private val novelRankingCacheStore: NovelRankingCacheStore,
) {

    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> = readOnly {
        val novels = novelReader.findNovelsByCursorOrderByCreatedAt(cursorRequest)
        return@readOnly novels.map { NovelModel.Main.from(it)(this) }
    }


    /**
     * 랭킹을 조회하여 반환한다.
     * 1. 랭킹 캐시를 조회한다.
     * 1-1. 캐시가 존재하면 캐시를 반환한다.
     * 1-2. 캐시가 존재하지 않으면 스코어링을 조회하여 캐시를 저장하고 반환한다.
     * 2. `novelIds`를 통해 소설을 조회한다.
     */
    fun getNovelModelsByRanking(days: Int, size: Int, page: Int): List<NovelModel.Main> {
        val cache: List<NovelAndScore>? =
            novelRankingCacheStore.getNovelIdRankingByPage(days = days, size = size, page = page)

        return readOnly {
            val novelIds = if (cache != null) {
                cache.map { it.novelId }
            } else {
                // 캐시가 없는 경우 전체 스코어링을 계산
                val scoresByOrder = novelScoringUseCase(days = days)
                // 계산 결과 전체 캐시 저장
                novelRankingCacheStore.saveNovelIdAndScoresAll(days, scoresByOrder)
                // 페이징 ID 추출
                scoresByOrder.map { it.novelId }.subList(
                    page * size,
                    minOf((page + 1) * size, scoresByOrder.size)
                )
            }
            // ID로 novel DB 조회
            val novels = novelReader.findNovelsByIdsIn(novelIds)
            return@readOnly novels.map { NovelModel.Main.from(it)() }
        }
    }


}

