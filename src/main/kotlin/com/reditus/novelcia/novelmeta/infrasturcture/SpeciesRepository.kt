package com.reditus.novelcia.novelmeta.infrasturcture

import com.reditus.novelcia.novelmeta.domain.Species
import org.springframework.data.jpa.repository.JpaRepository

interface SpeciesRepository : JpaRepository<Species, Long> {
    fun findByNameIn(names: List<String>): List<Species>
}