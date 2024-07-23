package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.domain.episode.Episode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EpisodeRepository: JpaRepository<Episode, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Episode e SET e.isDeleted = true, e.version = e.version + 1 WHERE e.id = :episodeId")
    fun softDeleteById(episodeId: Long): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Episode e SET e.isDeleted = true, e.version = e.version + 1 WHERE e.novel.id = :novelId")
    fun softDeleteAllByNovelId(novelId: Long): Int

    @Query("SELECT e FROM Episode e JOIN FETCH e.novel WHERE e.id = :id AND e.isDeleted = false")
    fun findByIdWithNovel(@Param("id") id: Long): Episode?
}