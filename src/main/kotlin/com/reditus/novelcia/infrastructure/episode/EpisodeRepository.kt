package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.domain.episode.Episode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface EpisodeRepository: JpaRepository<Episode, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Episode e SET e.isDeleted = true WHERE e.id = :episodeId")
    fun softDeleteById(episodeId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Episode e SET e.isDeleted = true WHERE e.novel.id = :novelId")
    fun softDeleteAllByNovelId(novelId: Long): Int
}