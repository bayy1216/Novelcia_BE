package com.reditus.novelcia.novelfavorite.domain


import com.reditus.novelcia.common.domain.BaseTimeEntity
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.user.domain.User
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["novel_id", "user_id"]
        )
    ]
)
class NovelFavorite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    var lastViewedEpisodeNumber: Int,
) : BaseTimeEntity(
) {
    companion object{
        fun create(
            novel: Novel,
            user: User,
        ): NovelFavorite {
            return NovelFavorite(
                novel = novel,
                user = user,
                lastViewedEpisodeNumber = Episode.INITIAL_EPISODE_NUMBER
            )
        }
    }
}