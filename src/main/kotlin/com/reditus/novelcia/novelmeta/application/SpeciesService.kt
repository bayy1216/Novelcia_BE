package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.common.domain.UpsertResult
import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.novelmeta.domain.SpeciesCommand
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novelmeta.infrasturcture.SpeciesRepository
import org.springframework.stereotype.Service

@Service
class SpeciesService(
    private val speciesRepository: SpeciesRepository,
) {
    fun getAllSpecies(): List<SpeciesModel> = readOnly {
        val species = speciesRepository.findAll()
        return@readOnly species.map { SpeciesModel.from(it)() }
    }

    fun upsertSpecies(commands: List<SpeciesCommand.Upsert>): UpsertResult<Long> = transactional {
        val existingSpeciesList = speciesRepository.findByNameIn(commands.map { it.name })
        val newSpecies = mutableListOf<Species>()
        commands.forEach { upsertCommand ->
            val existingSpecies = existingSpeciesList.find { it.name == upsertCommand.name }
            if (existingSpecies != null) {
                existingSpecies.update(upsertCommand)
            } else {
                val species = Species.create(upsertCommand)
                newSpecies.add(species)
            }
        }

        val species = commands.map { Species.create(it) }
        speciesRepository.saveAll(species)

        return@transactional UpsertResult(
            insertedCount = newSpecies.size,
            insertedIds = newSpecies.map(Species::id),
            updatedCount = existingSpeciesList.size
        )
    }
}