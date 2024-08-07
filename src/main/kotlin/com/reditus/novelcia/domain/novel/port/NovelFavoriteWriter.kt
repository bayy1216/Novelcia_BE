package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.NovelFavorite
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NovelFavoriteWriter {
    fun save(novelFavorite: NovelFavorite): NovelFavorite

    @Query("DELETE FROM NovelFavorite nf WHERE nf.user.id = :userId AND nf.novel.id = :novelId")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    fun deleteByUserIdAndNovelId(
        @Param("userId") userId: Long,
        @Param("novelId") novelId: Long,
    )
}