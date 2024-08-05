package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Species
import org.springframework.data.jpa.repository.JpaRepository

interface SpeciesRepository : JpaRepository<Species, Long> {
    fun findByNameIn(names: List<String>): List<Species>
}