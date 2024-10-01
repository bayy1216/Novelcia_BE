package com.reditus.novelcia.novel.infrastructure.adapter

import com.reditus.novelcia.novel.domain.NovelFavorite
import com.reditus.novelcia.novel.application.port.NovelFavoriteWriter
import com.reditus.novelcia.novel.infrastructure.NovelFavoriteRepository
import org.springframework.stereotype.Repository

@Repository
class NovelFavoriteWriterImpl(
    private val novelFavoriteRepository: NovelFavoriteRepository,
) : NovelFavoriteWriter {
    override fun save(novelFavorite: NovelFavorite): NovelFavorite {
        return novelFavoriteRepository.save(novelFavorite)
    }

    override fun deleteByUserIdAndNovelId(userId: Long, novelId: Long) {
        val affected = novelFavoriteRepository.deleteByNovelIdAndUserId(novelId, userId)
        if (affected == 0) {
            throw NoSuchElementException("해당 선호작이 존재하지 않습니다.")
        }
    }
}