package com.reditus.novelcia.infrastructure.novel.adapter

import com.reditus.novelcia.domain.novel.NovelFavorite
import com.reditus.novelcia.domain.novel.port.NovelFavoriteWriter
import com.reditus.novelcia.infrastructure.novel.NovelFavoriteRepository
import org.springframework.stereotype.Repository

@Repository
class NovelFavoriteWriterImpl(
    private val novelFavoriteRepository: NovelFavoriteRepository,
) : NovelFavoriteWriter{
    override fun save(novelFavorite: NovelFavorite): NovelFavorite {
        return novelFavoriteRepository.save(novelFavorite)
    }

    override fun deleteByUserIdAndNovelId(userId: Long, novelId: Long) {
        val affected = novelFavoriteRepository.deleteByNovelIdAndUserId(novelId, userId)
        if (affected == 0L) {
            throw NoSuchElementException("해당 선호작이 존재하지 않습니다.")
        }
    }
}