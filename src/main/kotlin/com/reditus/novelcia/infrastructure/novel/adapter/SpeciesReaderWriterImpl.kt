package com.reditus.novelcia.infrastructure.novel.adapter

import com.reditus.novelcia.domain.novel.Species
import com.reditus.novelcia.domain.novel.port.SpeciesReader
import com.reditus.novelcia.domain.novel.port.SpeciesWriter
import com.reditus.novelcia.infrastructure.novel.SpeciesRepository
import org.springframework.stereotype.Repository

@Repository
class SpeciesReaderWriterImpl(
    private val speciesRepository: SpeciesRepository,
) : SpeciesReader, SpeciesWriter {
    override fun getSpeciesAll(): List<Species> {
        return speciesRepository.findAll()
    }

    override fun getSpeciesByNamesIn(names: List<String>): List<Species> {
        return speciesRepository.findByNameIn(names)
    }

    override fun saveAll(species: List<Species>): List<Species> {
        return speciesRepository.saveAll(species)
    }
}