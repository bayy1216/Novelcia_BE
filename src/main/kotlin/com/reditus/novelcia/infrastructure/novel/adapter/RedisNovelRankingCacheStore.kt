package com.reditus.novelcia.infrastructure.novel.adapter

import com.reditus.novelcia.domain.novel.port.NovelRankingCacheStore
import com.reditus.novelcia.domain.novel.usecase.NovelAndScore
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Component
class RedisNovelRankingCacheStore(
    private val redisTemplate: RedisTemplate<String, String>,
) : NovelRankingCacheStore {
    /**
     * ZSet을 이용하여 랭킹을 조회한다.
     */
    override fun getNovelIdRankingByPage(days: Int, size: Int, page: Int): List<NovelAndScore>? {
        val key = getRankingKey(days)
        val start = page * size
        val end = start + size - 1

        val rankingPage = redisTemplate.opsForZSet()
            .rangeWithScores(key, start.toLong(), end.toLong())

        if (rankingPage.isNullOrEmpty()) {
            return null
        }

        return rankingPage.map {
            NovelAndScore(it.value!!.toLong(), it.value!!.toDouble())
        }
    }

    /**
     * ZSet에 소설 ID와 스코어를 저장한다.
     * 1. 기존 데이터 삭제(최근 기간에 해당하는 데이터이므로 전체 삭제 후 다시 저장)
     * 2. 소설 ID와 스코어 ZSet에 ZADD
     */
    override fun saveNovelIdAndScoresAll(days: Int, novelAndScores: List<NovelAndScore>) {
        val key = getRankingKey(days)
        redisTemplate.opsForZSet().removeRange(key, 0, -1)

        val dataToAdd = novelAndScores.map {
            ZSetOperations.TypedTuple.of(it.novelId.toString(), it.score)
        }.toSet()

        redisTemplate.opsForZSet().add(key, dataToAdd)
        // 1시간 10분후 만료(방어 코드)
        redisTemplate.expire(key, 70 * 60, java.util.concurrent.TimeUnit.SECONDS)
    }

    private fun getRankingKey(days: Int): String {
        return "novelRanking:days:$days"
    }
}