package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Species

interface SpeciesWriter {
    fun saveAll(species: List<Species>) : List<Species>
}