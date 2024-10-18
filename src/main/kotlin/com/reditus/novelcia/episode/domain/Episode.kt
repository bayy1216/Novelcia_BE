package com.reditus.novelcia.episode.domain


import com.reditus.novelcia.common.domain.BaseModifiableEntity
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.ReadAuthority
import com.reditus.novelcia.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(
    name = "novel_episode",
    uniqueConstraints = [
        UniqueConstraint(name = "uk__novel_id__episode_number", columnNames = ["novel_id", "episode_number"]),
    ],
    indexes = [
        Index(name = "idx__novel_id__episode_number", columnList = "novel_id, episode_number"),
    ]
)
@SQLDelete(sql = "UPDATE novel_episode SET deleted_at = current_timestamp() WHERE id = ?")
class Episode(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, length = 12000)
    var content: String,

    @Column(nullable = false)
    var episodeNumber: Int,

    @Column(nullable = true)
    var deletedAt: LocalDateTime?,

    @Column(nullable = false)
    var authorComment: String,

    @Enumerated(EnumType.STRING)
    var readAuthority: ReadAuthority,

    @Column(nullable = false)
    var viewsCount: Int,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "novel_id", nullable = false)
    val novel: Novel,

    @Version
    var version: Int = 0, // 변경감지로 인한 벌크연산 lost update 방어
) : BaseModifiableEntity() {

    val novelId: Long
        get() = novel.id

    fun canRead(user: User) : Boolean {
        if(user.isAdmin || readAuthority == ReadAuthority.FREE)
            return true
        if(user.isMemberShipValid && readAuthority == ReadAuthority.MEMBER_SHIP)
            return true

        return false
    }

    fun canEdit(value: Long): Boolean {
        return novel.isAuthor(value)
    }

    fun patch(command: EpisodeCommand.Patch) {
        command.title?.let { title = it }
        command.content?.let { content = it }
        command.authorComment?.let { authorComment = it }
        command.readAuthority?.let { readAuthority = it }
    }

    companion object{
        const val INITIAL_EPISODE_NUMBER = 1

        fun create(
            novel: Novel,
            episodeNumber: Int,
            command: EpisodeCommand.Create,
        ) = Episode(
            title = command.title,
            content = command.content,
            episodeNumber = episodeNumber,
            authorComment = command.authorComment,
            readAuthority = command.readAuthority,
            deletedAt = null,
            novel = novel,
            viewsCount = 0,
        )

        fun fixture(
            title: String = "title",
            content: String = "content",
            episodeNumber: Int = 1,
            deletedAt: LocalDateTime? = null,
            authorComment: String = "authorComment",
            readAuthority: ReadAuthority = ReadAuthority.FREE,
            novel: Novel = Novel.fixture(),
        ) = Episode(
            title = title,
            content = content,
            episodeNumber = episodeNumber,
            deletedAt = deletedAt,
            authorComment = authorComment,
            readAuthority = readAuthority,
            novel = novel,
            viewsCount = 0,
        )
    }
}

inline fun <T> Episode.authAsAuthor(
    condition: Boolean,
    message: String = "해당 에피소드를 수정할 권한이 없습니다.",
    action: Episode.() -> T,
): T {
    if (!condition) {
        throw NoPermissionException(message)
    }
    return this.action()
}

class EpisodeCommand{
    class Create(
        val title: String,
        val content: String,
        val authorComment: String,
        val readAuthority: ReadAuthority,
    )

    class Patch(
        val title: String?,
        val content: String?,
        val authorComment: String?,
        val readAuthority: ReadAuthority?,
    )
}