package com.reditus.novelcia.novel.domain.application

import com.reditus.novelcia.common.domain.CursorRequest
import com.reditus.novelcia.novel.domain.port.NovelRankingCacheStore
import com.reditus.novelcia.novel.domain.port.NovelReader
import com.reditus.novelcia.novel.domain.usecase.NovelIdAndScore
import com.reditus.novelcia.novel.domain.usecase.NovelScoringUseCase
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
        val localLockCheckNum = lockCheckNum

        val cache: List<NovelIdAndScore>? =
            novelRankingCacheStore.getNovelIdRankingByPage(days = days, size = size, page = page)

        if (cache != null) { // 캐시가 존재하면 캐시를 반환 early return
            return readOnly {
                val novels = novelReader.findNovelsByIdsIn(cache.map { it.novelId })
                return@readOnly novels.map { NovelModel.Main.from(it)() }
            }
        }

        // A. 스케줄링 노드의 실패로 인해 캐시가 없는경우(방어로직)
        // B. 최근 기간동안 `event`가 하나도 없어서 스코어링 `novel`이 없는 경우
        // -> 실시간으로 스코어링을 조회하여 캐시를 저장한다.
        return cacheUpdateLock.withLock {
            if (localLockCheckNum != lockCheckNum) {
                // 다른 쓰레드에 의해 캐시가 갱신되었다면, 현재쓰레드가 진입한 시점에서 lockCheckNum은 증가하였다.
                // 즉, 같지 않다면, 캐시 조회전에 둘다 같은 lockCheckNum이였고, 캐시 갱신이 일어났다.
                // 같다면, 캐시가 갱신이 안되었고, 현재 쓰레드가 락을 점유하였다.
                // 같은데, 다른 쓰레드가 캐시를 갱신하였다면, 해당 임계구역을 진입하지 못했다.
                // Volatile 변수를 사용했기 때문에 CPU 캐시에 저장되지 않고, 메인 메모리에 저장되어 쓰레드간 공유된다.
                novelRankingCacheStore.getNovelIdRankingByPage(days = days, size = size, page = page)?.let {
                    val novelIds = it.map { novelAndScore -> novelAndScore.novelId }
                    return@withLock readOnly {
                        val novels = novelReader.findNovelsByIdsIn(novelIds)
                        return@readOnly novels.map { novel -> NovelModel.Main.from(novel)() }
                    }
                }
            }


            log.warn("NovelScoringUseCase trigger by cache miss (days: $days, lockCheckNum: $lockCheckNum)")
            val scoresByOrder = novelScoringUseCase(days = days) // TX BLOCK
            if (scoresByOrder.isEmpty()) { // 스코어링 결과가 없으면 early return
                return@withLock emptyList()
            }

            novelRankingCacheStore.saveNovelIdAndScoresAll(days, scoresByOrder) // 캐시 저장
            lockCheckNum += 1 // 캐시 업데이트 했으므로 lockCheckNum 증가
            val novelIds = scoresByOrder.map { it.novelId }
            return@withLock readOnly { // 소설 DB 조회, // TX BLOCK
                val novels = novelReader.findNovelsByIdsIn(novelIds)
                return@readOnly novels.map { NovelModel.Main.from(it)() }
            }
        }
    }

    companion object {
        private val cacheUpdateLock = ReentrantLock()
        @Volatile // CPU 캐시 불일치 방지, JIT 최적화 방지
        private var lockCheckNum = 0
        private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }


}

