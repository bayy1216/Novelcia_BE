package com.reditus.novelcia.novel.infrastructure

import com.reditus.novelcia.novel.domain.NovelAndSpecies
import org.springframework.data.jpa.repository.JpaRepository

interface NovelAndSpeciesRepository: JpaRepository<NovelAndSpecies, Long> {
    fun findAllByNovelId(novelId: Long): List<NovelAndSpecies>
}