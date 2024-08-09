package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.domain.episode.EpisodeLike
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