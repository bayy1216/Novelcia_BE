package com.reditus.novelcia.domain.novel


import jakarta.persistence.*

@Entity
@Table(name = "novel_tag")
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var name: String,

    @Column
    var colorHexCode: String,
) {
    fun update(command: TagCommand.Update) {
        name = command.name
        colorHexCode = command.colorHexCode
    }

    companion object{
        fun create(command: TagCommand.Create): Tag {
            return Tag(
                name = command.name,
                colorHexCode = command.colorHexCode,
            )
        }
    }
}

class TagCommand {
    data class Create(
        val name: String,
        val colorHexCode: String,
    )

    data class Update(
        val name: String,
        val colorHexCode: String,
    )
}