package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.NovelFavorite

interface NovelFavoriteWriter {
    fun save(novelFavorite: NovelFavorite): NovelFavorite

    fun deleteByUserIdAndNovelId(userId: Long, novelId: Long)
}