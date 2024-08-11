package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.NovelFavorite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NovelFavoriteRepository : JpaRepository<NovelFavorite, Long> {
    fun findByUserIdAndNovelId(userId: Long, novelId: Long): NovelFavorite?
    @Query("DELETE FROM NovelFavorite nf WHERE nf.user.id = :userId AND nf.novel.id = :novelId")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    fun deleteByNovelIdAndUserId(
        @Param("novelId")
        novelId: Long,
        @Param("userId")
        userId: Long
    ): Int
}