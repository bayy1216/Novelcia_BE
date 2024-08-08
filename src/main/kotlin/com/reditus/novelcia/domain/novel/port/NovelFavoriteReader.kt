package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.OffsetRequest
import com.reditus.novelcia.domain.novel.application.NovelModel
import org.springframework.data.domain.Page

interface NovelFavoriteReader {
    fun getUserFavoriteNovelPage(userId: Long, offsetRequest: OffsetRequest): Page<NovelModel.UserFavorite>
}