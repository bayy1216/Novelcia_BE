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

    @OneToMany(mappedBy = "novel", cascade = [CascadeType.ALL], orphanRemoval = true)
    val novelAndTags: MutableList<NovelAndTag> = mutableListOf(),
) : BaseTimeEntity() {

    val authorId: Long
        get() = author.id

    val tags : List<Tag>
        get() = novelAndTags.map { it.tag }

    fun update(command: NovelCommand.Update, tags: List<Tag>) {
        title = command.title
        description = command.description
        thumbnailImageUrl = command.thumbnailImageUrl
        tags.forEach {tag->
            val existNovelAndTag: NovelAndTag? = novelAndTags.find { it.tag == tag }
            if(existNovelAndTag == null) {
                addTags(tag)
            }else{
                novelAndTags.remove(existNovelAndTag)
            }
        }
    }

    fun addTags(tag: Tag) {
        val newNovelAndTag = NovelAndTag(novel = this, tag = tag)
        novelAndTags.add(newNovelAndTag)
    }

    companion object{
        fun create(author: User, command: NovelCommand.Create, tags: List<Tag>): Novel {
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
                readAuthority = ReadAuthority.ALL,
            ).apply {
                tags.forEach { addTags(it) }
            }
        }
    }
}

enum class ReadAuthority {
    ALL, MEMBER_SHIP, PAYMENT
}

class NovelCommand {
    class Create(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
    )

    class Update(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
    )
}