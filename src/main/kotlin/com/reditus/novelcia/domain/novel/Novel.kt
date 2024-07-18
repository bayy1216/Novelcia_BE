package com.reditus.novelcia.domain.novel

import jakarta.persistence.Entity

import com.reditus.novelcia.domain.BaseTimeEntity
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*
@Entity
class Novel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    var thumbnailImageUrl: String?,

    @Column(nullable = false)
    var viewCount: Long,

    @Column(nullable = false)
    var likeCount: Long,

    @Column(nullable = false)
    var favoriteCount: Long,

    var alarmCount: Long,

    var episodeCount: Long,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    var readAuthority: ReadAuthority,
) : BaseTimeEntity() {

    val authorId: Long
        get() = author.id

    fun update(command: NovelCommand.Update) {
        title = command.title
        description = command.description
        thumbnailImageUrl = command.thumbnailImageUrl
    }

    companion object{
        fun create(author: User, command: NovelCommand.Register): Novel {
            return Novel(
                author = author,
                title = command.title,
                description = command.description,
                thumbnailImageUrl = command.thumbnailImageUrl,
                viewCount = 0,
                likeCount = 0,
                favoriteCount = 0,
                alarmCount = 0,
                episodeCount = 0,
                isDeleted = false,
                readAuthority = ReadAuthority.ALL
            )
        }
    }
}

enum class ReadAuthority {
    ALL, MEMBER_SHIP, PAYMENT
}

class NovelCommand {
    class Register(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tags: List<String>,
    )

    class Update(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tags: List<String>,
    )
}