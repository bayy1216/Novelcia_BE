package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.common.domain.OffsetResponse
import com.reditus.novelcia.novelfavorite.domain.NovelFavorite
import com.reditus.novelcia.novel.application.port.NovelReader
import com.reditus.novelcia.user.application.port.UserReader
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.application.model.NovelModel
import org.springframework.stereotype.Service

@Service
class NovelFavoriteService(
    private val userReader: UserReader,
    private val novelReader: NovelReader,
    private val novelFavoriteReader: NovelFavoriteReader,
    private val novelFavoriteWriter: NovelFavoriteWriter,
) {
    fun getFavoriteNovels(
        loginUserId: LoginUserId,
        offsetRequest: OffsetRequest,
    ): OffsetResponse<NovelFavoriteModel.UserFavorite> = readOnly {
        val page = novelFavoriteReader.getUserFavoriteNovelPage(loginUserId.value, offsetRequest)
        return@readOnly OffsetResponse(
            data = page.content,
            totalElements = page.totalElements,
        )
    }

    fun addFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {
        val user = userReader.getReferenceById(loginUserId.value)
        val novel = novelReader.getNovelById(novelId)
        val novelFavorite = NovelFavorite.create(novel, user)
        novelFavoriteWriter.save(novelFavorite)
    }

    fun deleteFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {
        novelFavoriteWriter.deleteByUserIdAndNovelId(loginUserId.value, novelId)
    }
}