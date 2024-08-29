package com.reditus.novelcia.episode.infrastructure

import com.reditus.novelcia.episode.domain.EpisodeLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface EpisodeLikeRepository : JpaRepository<EpisodeLike, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM EpisodeLike el WHERE el.episode.id = :episodeId AND el.user.id = :userId")
    fun deleteByEpisodeIdAndUserId(episodeId: Long, userId: Long): Int

    fun findByEpisodeIdAndUserId(episodeId: Long, userId: Long): EpisodeLike?

    fun countByEpisodeId(episodeId: Long): Long
}