package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Novel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NovelRepository : JpaRepository<Novel, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Novel n SET n.isDeleted = true WHERE n.id = :novelId")
    fun softDelete(@Param("novelId")novelId: Long) : Int
}