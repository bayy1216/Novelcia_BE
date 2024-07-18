package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.BaseTimeEntity
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["novel", "episode_id", "user_id"]
        )
    ]
)
class EpisodeView(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    val episode: Episode,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BaseTimeEntity() {
}