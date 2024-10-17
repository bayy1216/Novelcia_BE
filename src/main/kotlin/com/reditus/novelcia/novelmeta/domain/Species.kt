package com.reditus.novelcia.novelmeta.domain

import com.reditus.novelcia.common.domain.BaseModifiableEntity
import jakarta.persistence.*

@Entity
@Table(name = "novel_species")
class Species(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(columnDefinition = "CHAR(7)", nullable = false, length = 7)
    var colorHexCode: String,
) : BaseModifiableEntity() {

    fun update(command: SpeciesCommand.Update) {
        this.name = command.name
        this.colorHexCode = command.colorHexCode
    }

    fun update(command: SpeciesCommand.Upsert) {
        this.colorHexCode = command.colorHexCode
    }
    companion object {
        fun create(command: SpeciesCommand.Upsert): Species {
            return Species(
                name = command.name,
                colorHexCode = command.colorHexCode,
            )
        }

        fun fixture(
            id: Long = 0L,
            name: String = "독점",
            colorHexCode: String = "#FFFFFF",
        ) = Species(
            id = id,
            name = name,
            colorHexCode = colorHexCode,
        )
    }

}

class SpeciesCommand {
    data class Update(
        val name: String,
        val colorHexCode: String,
    ) {
        init {
            require(colorHexCode.length == 7) { "색상 코드는 7자리여야 합니다." }
            require(colorHexCode.startsWith("#")) { "색상 코드는 #으로 시작해야 합니다." }
        }
    }

    data class Upsert(
        val name: String,
        val colorHexCode: String,
    ) {
        init {
            require(name.isNotBlank()) { "소설 분류 이름은 필수입니다." }
            require(colorHexCode.length == 7) { "색상 코드는 7자리여야 합니다." }
            require(colorHexCode.startsWith("#")) { "색상 코드는 #으로 시작해야 합니다." }
        }
    }
}
