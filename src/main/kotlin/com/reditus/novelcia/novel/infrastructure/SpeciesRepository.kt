package com.reditus.novelcia.novel.infrastructure

import com.reditus.novelcia.novel.domain.Species
import org.springframework.data.jpa.repository.JpaRepository

interface SpeciesRepository : JpaRepository<Species, Long> {
    fun findByNameIn(names: List<String>): List<Species>
}