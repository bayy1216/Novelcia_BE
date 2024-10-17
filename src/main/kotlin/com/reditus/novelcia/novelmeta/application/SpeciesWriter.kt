package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Species

interface SpeciesWriter {
    fun saveAll(species: List<Species>) : List<Species>
}