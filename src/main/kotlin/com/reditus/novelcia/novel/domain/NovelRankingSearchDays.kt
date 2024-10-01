package com.reditus.novelcia.novel.domain

enum class NovelRankingSearchDays(val value: Int) {
    DAY_1(1),
    DAY_7(7),
    DAY_30(30),;

    companion object {
        fun from(days: Int): NovelRankingSearchDays {
            return entries.find { it.value == days }
                ?: throw IllegalArgumentException("해당하는 일자가 없습니다. days: $days")
        }
    }
}