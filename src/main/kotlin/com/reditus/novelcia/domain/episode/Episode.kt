package com.reditus.novelcia.domain.episode


import com.reditus.novelcia.domain.BaseModifiableEntity
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
@Table(
    name = "novel_episode",
    indexes = [
        Index(name = "idx__novel_id__is_deleted__episode_number", columnList = "novel_id, is_deleted, episode_number"),
    ]
)
class Episode(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, length = 12000)
    var content: String,

    @Column(nullable = false)
    var episodeNumber: Int,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Column(nullable = false)
    var authorComment: String,

    @Enumerated(EnumType.STRING)
    var readAuthority: ReadAuthority,

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

    companion object{
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
            isDeleted = false,
            novel = novel,
        )

        fun fixture(
            title: String = "title",
            content: String = "content",
            episodeNumber: Int = 1,
            isDeleted: Boolean = false,
            authorComment: String = "authorComment",
            readAuthority: ReadAuthority = ReadAuthority.FREE,
            novel: Novel,
        ) = Episode(
            title = title,
            content = content,
            episodeNumber = episodeNumber,
            isDeleted = isDeleted,
            authorComment = authorComment,
            readAuthority = readAuthority,
            novel = novel
        )
    }
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