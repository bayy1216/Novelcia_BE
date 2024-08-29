package com.reditus.novelcia.episode.domain


import com.reditus.novelcia.common.domain.BaseTimeEntity
import com.reditus.novelcia.user.domain.User
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["episode_id", "user_id"]
        )
    ]
)
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
    companion object {
        fun create(episode: Episode, user: User): EpisodeLike {
            return EpisodeLike(
                episode = episode,
                user = user
            )
        }
    }
}