package com.reditus.novelcia.novelmeta.interfaces

import com.reditus.novelcia.novelmeta.domain.SpeciesCommand
import io.swagger.v3.oas.annotations.media.Schema

class SpeciesReq {
    data class Upsert(
        @Schema(example = "독점")
        val name: String,
        @Schema(example = "#FFFFFF")
        val colorHexCode: String,
    )

    data class UpsertMany(
        val species: List<Upsert>,
    ) {
        fun toCommands(): List<SpeciesCommand.Upsert> = species.map {
            SpeciesCommand.Upsert(
                name = it.name,
                colorHexCode = it.colorHexCode,
            )
        }
    }
}