package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.Species

interface SpeciesReader {
    fun findSpeciesAll(): List<Species>
    fun findSpeciesByNamesIn(names: List<String>): List<Species>
}