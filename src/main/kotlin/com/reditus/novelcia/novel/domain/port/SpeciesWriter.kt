package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.Species

interface SpeciesWriter {
    fun saveAll(species: List<Species>) : List<Species>
}