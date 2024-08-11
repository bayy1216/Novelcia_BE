package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.NovelFavorite

interface NovelFavoriteWriter {
    fun save(novelFavorite: NovelFavorite): NovelFavorite

    fun deleteByUserIdAndNovelId(userId: Long, novelId: Long)
}