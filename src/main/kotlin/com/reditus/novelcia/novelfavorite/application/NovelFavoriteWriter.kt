package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.novelfavorite.domain.NovelFavorite

interface NovelFavoriteWriter {
    fun save(novelFavorite: NovelFavorite): NovelFavorite

    fun deleteByUserIdAndNovelId(userId: Long, novelId: Long)
}