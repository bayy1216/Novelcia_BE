package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Species

interface SpeciesReader {
    fun findSpeciesAll(): List<Species>
    fun findSpeciesByNamesIn(names: List<String>): List<Species>
}