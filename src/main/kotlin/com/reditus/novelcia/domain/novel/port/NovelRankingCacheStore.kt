package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.usecase.NovelIdAndScore

interface NovelRankingCacheStore {
    fun getNovelIdRankingByPage(days: Int, size: Int, page: Int): List<NovelIdAndScore>?
    fun saveNovelIdAndScoresAll(days: Int, novelIdAndScores: List<NovelIdAndScore>)
}