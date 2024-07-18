package com.reditus.novelcia.domain.novel


import jakarta.persistence.*

@Entity
@Table(name = "novel_tag")
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var name: String,
) {
}