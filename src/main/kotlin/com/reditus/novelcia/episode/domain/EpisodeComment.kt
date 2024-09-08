package com.reditus.novelcia.episode.domain


import com.reditus.novelcia.common.domain.BaseModifiableEntity
import com.reditus.novelcia.user.domain.User
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

    fun update(command: EpisodeCommentCommand.Update) {
        this.content = command.content
    }

}

class EpisodeCommentCommand{
    class Create(
        val parentCommentId: Long?,
        val content: String,
    )

    class Update(
        val content: String,
    )
}