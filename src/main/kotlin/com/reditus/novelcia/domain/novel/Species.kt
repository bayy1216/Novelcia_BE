package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "novel_species")
class Species(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var name: String,
) : BaseTimeEntity() {

}
