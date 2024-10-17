package com.reditus.novelcia.novel.domain

import com.reditus.novelcia.common.domain.BaseTimeEntity
import com.reditus.novelcia.novelmeta.domain.Tag
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["novel_id", "tag_id"]
        )
    ]
)
class NovelAndTag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag,
) : BaseTimeEntity() {
    companion object {
        fun create(novel: Novel, tag: Tag): NovelAndTag {
            return NovelAndTag(novel = novel, tag = tag)
        }
    }
}