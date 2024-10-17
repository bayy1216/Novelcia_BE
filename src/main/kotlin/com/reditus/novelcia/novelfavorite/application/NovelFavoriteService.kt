package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.common.domain.OffsetResponse
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.novelfavorite.domain.NovelFavorite
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.novelfavorite.infrastructure.NovelFavoriteQueryRepository
import com.reditus.novelcia.novelfavorite.infrastructure.NovelFavoriteRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class NovelFavoriteService(
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository,
    private val novelFavoriteRepository: NovelFavoriteRepository,
    private val novelFavoriteQueryRepository: NovelFavoriteQueryRepository
) {
    fun getFavoriteNovels(
        loginUserId: LoginUserId,
        offsetRequest: OffsetRequest,
    ): OffsetResponse<NovelFavoriteModel.UserFavorite> = readOnly {
        val page = novelFavoriteQueryRepository.getUserFavoriteNovelPage(loginUserId.value, offsetRequest)
        return@readOnly OffsetResponse(
            data = page.content,
            totalElements = page.totalElements,
        )
    }

    fun addFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {
        val user = userRepository.getReferenceById(loginUserId.value)
        val novel = novelRepository.findByIdOrThrow(novelId)
        val novelFavorite = NovelFavorite.create(novel, user)
        novelFavoriteRepository.save(novelFavorite)
    }

    fun deleteFavoriteNovel(
        loginUserId: LoginUserId,
        novelId: Long,
    ) = transactional {
        novelFavoriteRepository.deleteByNovelIdAndUserId(userId = loginUserId.value, novelId = novelId)
    }
}