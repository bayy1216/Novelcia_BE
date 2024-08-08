package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.OffsetRequest
import com.reditus.novelcia.domain.OffsetResponse
import com.reditus.novelcia.domain.novel.NovelFavorite
import com.reditus.novelcia.domain.novel.port.NovelFavoriteReader
import com.reditus.novelcia.domain.novel.port.NovelFavoriteWriter
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.user.port.UserReader
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
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
    ): OffsetResponse<NovelModel.UserFavorite> = readOnly {
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