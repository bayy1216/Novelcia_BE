package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.novel.domain.NovelMeta

class SpeciesModel(
    val id: Long,
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(species: Species): TxScope.() -> SpeciesModel = {
            SpeciesModel(
                id = species.id,
                name = species.name,
                colorHexCode = species.colorHexCode,
            )
        }

        fun from(speciesData: NovelMeta.SpeciesData): TxScope.() -> SpeciesModel = {
            SpeciesModel(
                id = speciesData.id,
                name = speciesData.name,
                colorHexCode = speciesData.colorHexCode,
            )
        }
    }
}
