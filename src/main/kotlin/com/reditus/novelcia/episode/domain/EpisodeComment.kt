package com.reditus.novelcia.episode.domain


import com.reditus.novelcia.common.domain.BaseModifiableEntity
import com.reditus.novelcia.novel.domain.Novel
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

    val episodeId: Long
        get() = episode.id

    val userId: Long
        get() = user.id

    fun update(command: EpisodeCommentCommand.Update) {
        this.content = command.content
    }

    companion object {
        fun create(
            command: EpisodeCommentCommand.Create,
            episode: Episode,
            user: User,
            parentComment: EpisodeComment?
        ) = EpisodeComment(
            episode = episode,
            user = user,
            content = command.content,
            isDeleted = false,
            parent = parentComment,
        )

        fun fixture(
            id: Long = 0L,
            episode: Episode = Episode.fixture(),
            user: User = User.fixture(),
            content: String = "content",
            isDeleted: Boolean = false,
            parent: EpisodeComment? = null,
        ) = EpisodeComment(
            id = id,
            episode = episode,
            user = user,
            content = content,
            isDeleted = isDeleted,
            parent = parent,
        )
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