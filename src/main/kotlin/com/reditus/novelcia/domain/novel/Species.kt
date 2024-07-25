package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.BaseModifiableEntity
import jakarta.persistence.*

@Entity
@Table(name = "novel_species")
class Species(
    @Id
    val name: String,
) : BaseModifiableEntity() {

}
