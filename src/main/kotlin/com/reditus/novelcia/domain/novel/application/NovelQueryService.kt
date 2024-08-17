package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.port.NovelRankingCacheStore
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.usecase.NovelAndScore
import com.reditus.novelcia.domain.novel.usecase.NovelScoringUseCase
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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

        if (cache != null) { // 캐시가 존재하면 캐시를 반환 early return
            return readOnly {
                val novels = novelReader.findNovelsByIdsIn(cache.map { it.novelId })
                return@readOnly novels.map { NovelModel.Main.from(it)() }
            }
        }

        val localLockCheckNum = lockCheckNum
        // A. 스케줄링 노드의 실패로 인해 캐시가 없는경우(방어로직)
        // B. 최근 기간동안 `event`가 하나도 없어서 스코어링 `novel`이 없는 경우
        // -> 실시간으로 스코어링을 조회하여 캐시를 저장한다.
        // API 서버간의 분산락은 사용하지 않는다 (복잡성을 줄이기 위해)
        return cacheUpdateLock.withLock {
            if (localLockCheckNum != lockCheckNum) { // 다른 스레드가 먼저 캐시를 업데이트 했을 경우
                novelRankingCacheStore.getNovelIdRankingByPage(days = days, size = size, page = page)?.let {
                    val novelIds = it.map { novelAndScore -> novelAndScore.novelId }
                    return@withLock readOnly {
                        val novels = novelReader.findNovelsByIdsIn(novelIds)
                        return@readOnly novels.map { NovelModel.Main.from(it)() }
                    }
                }
            }

            lockCheckNum += 1 // lock 내부이므로 AtomicInt 필요하지 않음
            log.warn("NovelScoringUseCase trigger by cache miss (days: $days, lockCheckNum: $lockCheckNum)")
            val scoresByOrder = novelScoringUseCase(days = days) // TX BLOCK
            if (scoresByOrder.isEmpty()) { // 스코어링 결과가 없으면 early return
                return@withLock emptyList()
            }

            novelRankingCacheStore.saveNovelIdAndScoresAll(days, scoresByOrder) // 캐시 저장
            val novelIds = scoresByOrder.map { it.novelId }
            return@withLock readOnly { // 소설 DB 조회, // TX BLOCK
                val novels = novelReader.findNovelsByIdsIn(novelIds)
                return@readOnly novels.map { NovelModel.Main.from(it)() }
            }
        }

    }

    companion object {
        private val cacheUpdateLock = ReentrantLock()
        private var lockCheckNum = 0
        private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }


}

