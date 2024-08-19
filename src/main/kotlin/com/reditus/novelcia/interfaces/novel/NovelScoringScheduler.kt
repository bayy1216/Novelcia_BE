package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.novel.port.NovelRankingCacheStore
import com.reditus.novelcia.domain.novel.usecase.NovelScoringUseCase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NovelScoringScheduler(
    private val novelScoringUseCase: NovelScoringUseCase,
    private val novelRankingCacheStore: NovelRankingCacheStore,
) {
    // 매 1시간 마다 스케줄링 cron
    @Scheduled(cron = "0 0 * * * *")
    fun scheduleScoring() {
        val days = listOf(1, 7, 30)
        days.forEach { day ->
            val scoresByOrder = novelScoringUseCase(days = day) // TX BLOCK

            if (scoresByOrder.isEmpty()) {
                return@forEach
            }
            novelRankingCacheStore.saveNovelIdAndScoresAll(day, scoresByOrder) // 캐시 저장
        }
    }
}