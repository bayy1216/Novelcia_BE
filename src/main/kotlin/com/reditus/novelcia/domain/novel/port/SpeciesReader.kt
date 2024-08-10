package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Species

interface SpeciesReader {
    fun findSpeciesAll(): List<Species>
    fun findSpeciesByNamesIn(names: List<String>): List<Species>
}