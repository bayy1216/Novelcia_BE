package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.OffsetRequest
import com.reditus.novelcia.domain.OffsetResponse
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service

@Service
class NovelFavoriteService {
    fun getFavoriteNovels(
        loginUserId: LoginUserId,
        offsetRequest: OffsetRequest,
    ) : OffsetResponse<NovelModel.UserFavorite> = readOnly {
        throw NotImplementedError()
    }

    fun addFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {

    }

    fun deleteFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {

    }
}