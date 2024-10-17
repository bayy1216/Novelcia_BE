package com.reditus.novelcia.novel.infrastructure

import com.reditus.novelcia.novel.domain.NovelAndTag
import org.springframework.data.jpa.repository.JpaRepository

interface NovelAndTagRepository: JpaRepository<NovelAndTag, Long> {
    fun findAllByNovelId(novelId: Long): List<NovelAndTag>
}