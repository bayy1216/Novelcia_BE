package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.usecase.NovelAndScore

interface NovelRankingCacheStore {
    fun getNovelIdRankingByPage(days: Int, size: Int, page: Int): List<NovelAndScore>?
    fun saveNovelIdAndScoresAll(days: Int, novelAndScores: List<NovelAndScore>)
}