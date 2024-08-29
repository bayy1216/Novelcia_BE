package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.usecase.NovelIdAndScore

interface NovelRankingCacheStore {
    fun getNovelIdRankingByPage(days: Int, size: Int, page: Int): List<NovelIdAndScore>?
    fun saveNovelIdAndScoresAll(days: Int, novelIdAndScores: List<NovelIdAndScore>)
}