package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.common.OffsetRequest
import com.reditus.novelcia.domain.novel.NovelFavorite
import com.reditus.novelcia.domain.novel.application.NovelModel
import org.springframework.data.domain.Page

interface NovelFavoriteReader {
    fun findByUserIdAndNovelId(userId: Long, novelId: Long): NovelFavorite?
    fun getUserFavoriteNovelPage(userId: Long, offsetRequest: OffsetRequest): Page<NovelModel.UserFavorite>
}