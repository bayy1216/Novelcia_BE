package com.reditus.novelcia.novelmeta.domain


import com.reditus.novelcia.common.domain.BaseTimeEntity
import com.reditus.novelcia.novel.domain.Novel
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["novel_id", "novel_species_id"]
        )
    ]
)
class NovelAndSpecies(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_species_id", nullable = false)
    val species: Species,
) : BaseTimeEntity() {
    companion object {
        fun create(novel: Novel, species: Species): NovelAndSpecies {
            return NovelAndSpecies(novel = novel, species = species)
        }
    }
}