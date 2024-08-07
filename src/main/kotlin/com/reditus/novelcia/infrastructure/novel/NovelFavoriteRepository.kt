package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.NovelFavorite
import org.springframework.data.jpa.repository.JpaRepository

interface NovelFavoriteRepository : JpaRepository<NovelFavorite, Long> {
    fun deleteByNovelIdAndUserId(novelId: Long, userId: Long): Long
}