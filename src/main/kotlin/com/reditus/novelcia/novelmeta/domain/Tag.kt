package com.reditus.novelcia.novelmeta.domain


import com.reditus.novelcia.common.domain.BaseModifiableEntity
import jakarta.persistence.*

@Entity
@Table(name = "novel_tag")
class Tag(
    @Id
    val name: String,

    @Column(nullable = false, length = 7, columnDefinition = "CHAR(7)")
    var colorHexCode: String,
) : BaseModifiableEntity() {
    fun update(command: TagCommand.Upsert) {
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
    ){
        init {
            require(name.isNotBlank()) { "태그 이름은 필수입니다." }
            require(colorHexCode.length == 7) { "색상 코드는 7자리여야 합니다." }
            require(colorHexCode.startsWith("#")) { "색상 코드는 #으로 시작해야 합니다." }
        }
    }
}