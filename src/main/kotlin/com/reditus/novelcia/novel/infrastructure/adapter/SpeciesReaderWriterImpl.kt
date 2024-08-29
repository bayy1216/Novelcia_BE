package com.reditus.novelcia.novel.infrastructure.adapter

import com.reditus.novelcia.novel.domain.Species
import com.reditus.novelcia.novel.domain.port.SpeciesReader
import com.reditus.novelcia.novel.domain.port.SpeciesWriter
import com.reditus.novelcia.novel.infrastructure.SpeciesRepository
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