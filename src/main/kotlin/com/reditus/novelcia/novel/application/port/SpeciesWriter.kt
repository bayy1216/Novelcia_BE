package com.reditus.novelcia.novel.application.port

import com.reditus.novelcia.novel.domain.Species

interface SpeciesWriter {
    fun saveAll(species: List<Species>) : List<Species>
}