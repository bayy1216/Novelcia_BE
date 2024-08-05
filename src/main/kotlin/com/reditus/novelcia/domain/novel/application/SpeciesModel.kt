package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.novel.Species
import com.reditus.novelcia.global.util.TxScope

class SpeciesModel(
    val id: Long,
    val name: String,
    val colorHexCode: String,
) {
    companion object {
        fun from(species: Species): TxScope.()-> SpeciesModel {
            return {
                SpeciesModel(
                    id = species.id,
                    name = species.name,
                    colorHexCode = species.colorHexCode,
                )
            }
        }
    }

}