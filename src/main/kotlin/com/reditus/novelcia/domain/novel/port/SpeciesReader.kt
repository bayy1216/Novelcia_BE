package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Species

interface SpeciesReader {
    fun getSpeciesAll(): List<Species>
    fun getSpeciesByNamesIn(names: List<String>): List<Species>
}