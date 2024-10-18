package com.reditus.novelcia.novel.infrastructure

import com.reditus.novelcia.novel.domain.Novel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NovelRepository : JpaRepository<Novel, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Novel n SET n.viewCount = n.viewCount + :count WHERE n.id = :novelId")
    fun addViewCount(@Param("novelId")novelId: Long, @Param("count")count: Long) : Int
}