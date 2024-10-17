package com.reditus.novelcia.novelmeta.infrasturcture

import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.novelmeta.application.SpeciesReader
import com.reditus.novelcia.novelmeta.application.SpeciesWriter
import org.springframework.stereotype.Repository

@Repository
class SpeciesReaderWriterImpl(
    private val speciesRepository: SpeciesRepository,
) : SpeciesReader, SpeciesWriter {
    override fun findSpeciesAll(): List<Species> {
        return speciesRepository.findAll()
    }

    override fun findSpeciesByNamesIn(names: List<String>): List<Species> {
        return speciesRepository.findByNameIn(names)
    }

    override fun saveAll(species: List<Species>): List<Species> {
        return speciesRepository.saveAll(species)
    }
}