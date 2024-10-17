package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.novelfavorite.domain.NovelFavorite
import com.reditus.novelcia.novel.application.model.NovelModel
import org.springframework.data.domain.Page

interface NovelFavoriteReader {
    fun findByUserIdAndNovelId(userId: Long, novelId: Long): NovelFavorite?
    fun getUserFavoriteNovelPage(userId: Long, offsetRequest: OffsetRequest): Page<NovelFavoriteModel.UserFavorite>
}