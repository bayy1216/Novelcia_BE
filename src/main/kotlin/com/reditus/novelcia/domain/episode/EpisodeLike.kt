package com.reditus.novelcia.domain.episode


import com.reditus.novelcia.domain.BaseTimeEntity
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
class EpisodeLike(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    val episode: Episode,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BaseTimeEntity() {
}