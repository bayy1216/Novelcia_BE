package com.reditus.novelcia.domain.episode


import com.reditus.novelcia.domain.BaseTimeEntity
import com.reditus.novelcia.domain.novel.Novel
import jakarta.persistence.*

@Entity
@Table(name = "novel_episode")
class Episode(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, length = 16383)
    var content: String,

    @Column(nullable = false)
    var episodeNumber: Int,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Column(nullable = false)
    var authorComment: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,
) : BaseTimeEntity() {

    companion object{
        fun fixture(
            title: String = "title",
            content: String = "content",
            episodeNumber: Int = 1,
            isDeleted: Boolean = false,
            authorComment: String = "authorComment",
            novel: Novel,
        ) = Episode(
            title = title,
            content = content,
            episodeNumber = episodeNumber,
            isDeleted = isDeleted,
            authorComment = authorComment,
            novel = novel
        )
    }
}