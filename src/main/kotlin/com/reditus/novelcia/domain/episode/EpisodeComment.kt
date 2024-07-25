package com.reditus.novelcia.domain.episode


import com.reditus.novelcia.domain.BaseModifiableEntity
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
class EpisodeComment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    val episode: Episode,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    val parent: EpisodeComment?,
) : BaseModifiableEntity() {
}