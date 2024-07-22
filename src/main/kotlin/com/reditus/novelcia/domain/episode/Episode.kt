package com.reditus.novelcia.domain.episode


import com.reditus.novelcia.domain.BaseTimeEntity
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "novel_episode")
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
) : BaseTimeEntity() {

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