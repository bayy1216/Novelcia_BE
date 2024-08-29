package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.novel.domain.NovelFavorite
import com.reditus.novelcia.novel.domain.application.NovelModel
import org.springframework.data.domain.Page

interface NovelFavoriteReader {
    fun findByUserIdAndNovelId(userId: Long, novelId: Long): NovelFavorite?
    fun getUserFavoriteNovelPage(userId: Long, offsetRequest: OffsetRequest): Page<NovelModel.UserFavorite>
}