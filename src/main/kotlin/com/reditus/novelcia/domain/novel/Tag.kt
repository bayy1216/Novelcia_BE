package com.reditus.novelcia.domain.novel


import com.reditus.novelcia.domain.BaseModifiableEntity
import jakarta.persistence.*

@Entity
@Table(name = "novel_tag")
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column
    var colorHexCode: String,
) : BaseModifiableEntity() {
    fun update(command: TagCommand.Upsert) {
        name = command.name
        colorHexCode = command.colorHexCode
    }

    companion object{
        fun create(command: TagCommand.Upsert): Tag {
            return Tag(
                name = command.name,
                colorHexCode = command.colorHexCode,
            )
        }

        fun fixture(
            name: String = "태그",
            colorHexCode: String = "#000000",
        ) = Tag(
            name=name,
            colorHexCode=colorHexCode,
        )
    }
}

class TagCommand {
    data class Upsert(
        val name: String,
        val colorHexCode: String,
    )
}