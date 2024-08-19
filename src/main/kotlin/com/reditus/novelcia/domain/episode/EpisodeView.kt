package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.common.BaseTimeEntity
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
@Table(
    indexes = [
        Index(name = "idx__episode_id__user_id", columnList = "episode_id, user_id"),
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

    val novelId: Long
        get() = novel.id
    val userId: Long
        get() = user.id
    val episodeId: Long
        get() = episode.id
}