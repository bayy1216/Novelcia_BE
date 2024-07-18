package com.reditus.novelcia.domain.novel

import jakarta.persistence.*

@Entity
@Table(name = "novel_species")
class Species(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Enumerated
    var speciesType: SpeciesType,
) {

}

enum class SpeciesType {
    PLUS, MONOPOLY, ORIGINAL
}