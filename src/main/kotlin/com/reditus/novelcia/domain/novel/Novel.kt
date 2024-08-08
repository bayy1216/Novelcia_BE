package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.BaseModifiableEntity
import jakarta.persistence.Entity

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

    @Column(nullable = false)
    var alarmCount: Long,

    @Column(nullable = false)
    var episodeCount: Long,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    var readAuthority: ReadAuthority,

    @OneToMany(mappedBy = "novel", cascade = [CascadeType.ALL], orphanRemoval = true)
    val novelAndTags: MutableList<NovelAndTag> = mutableListOf(),

    @OneToMany(mappedBy = "novel", cascade = [CascadeType.ALL], orphanRemoval = true)
    val novelAndSpeciesList: MutableList<NovelAndSpecies> = mutableListOf(),

    @Version
    var version: Int = 0, // 변경감지로 인한 벌크연산 lost update 방어
) : BaseModifiableEntity() {

    val authorId: Long
        get() = author.id

    val tags: List<Tag>
        get() = novelAndTags.map { it.tag }

    val speciesList: List<Species>
        get() = novelAndSpeciesList.map { it.species }

    val isFree: Boolean
        get() = readAuthority == ReadAuthority.FREE

    fun isAuthor(userId: Long): Boolean {
        return authorId == userId
    }

    /**
     * 소설 정보 수정 - PUT과 같은 대치의 역할을 한다.
     * - 기존 태그에 없다면 추가한다.
     * - update 인자의 태그가 기존 태그에 없다면 추가한다
     * - update 인자의 태그가 기존 태그에 있다면 기존 태그에서 제거한다
     */
    fun update(
        command: NovelCommand.Update,
        updateTagsSet: Set<Tag>,
        updateSpeciesSet: Set<Species>,
    ) {
        title = command.title
        description = command.description
        thumbnailImageUrl = command.thumbnailImageUrl

        val currentTagsSet = tags.toSet()

        val toAddTags = updateTagsSet.minus(currentTagsSet)
        val toRemoveTags = currentTagsSet.minus(updateTagsSet)

        toAddTags.forEach { addNovelAndTag(it) }
        toRemoveTags.forEach { removeTag ->
            val novelAndTag = novelAndTags.find { it.tag == removeTag }
            novelAndTags.remove(novelAndTag)
        }

        val currentSpeciesSet = speciesList.toSet()

        val toAddSpecies = updateSpeciesSet.minus(currentSpeciesSet)
        val toRemoveSpecies = currentSpeciesSet.minus(updateSpeciesSet)

        toAddSpecies.forEach { addNovelAndSpecies(it) }
        toRemoveSpecies.forEach { removeSpecies ->
            val novelAndSpecies = novelAndSpeciesList.find { it.species == removeSpecies }
            novelAndSpeciesList.remove(novelAndSpecies)
        }
    }

    fun addNovelAndTag(tag: Tag) {
        val newNovelAndTag = NovelAndTag.create(novel = this, tag = tag)
        novelAndTags.add(newNovelAndTag)
    }

    fun addNovelAndSpecies(species: Species) {
        val newNovelAndSpecies = NovelAndSpecies(novel = this, species = species)
        novelAndSpeciesList.add(newNovelAndSpecies)
    }

    companion object {
        fun create(
            author: User,
            command: NovelCommand.Create,
            tags: List<Tag>,
            speciesList : List<Species>,
        ): Novel {
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
                readAuthority = ReadAuthority.FREE,
            ).apply {
                tags.forEach { addNovelAndTag(it) }
                speciesList.forEach { addNovelAndSpecies(it) }
            }
        }

        fun fixture(
            id: Long = 0L,
            author: User,
            title: String = "title",
            description: String = "description",
            thumbnailImageUrl: String? = null,
            viewCount: Long = 0,
            likeCount: Long = 0,
            favoriteCount: Long = 0,
            alarmCount: Long = 0,
            episodeCount: Long = 0,
            isDeleted: Boolean = false,
            readAuthority: ReadAuthority = ReadAuthority.FREE,
        ) = Novel(
            id = id,
            author = author,
            title = title,
            description = description,
            thumbnailImageUrl = thumbnailImageUrl,
            viewCount = viewCount,
            likeCount = likeCount,
            favoriteCount = favoriteCount,
            alarmCount = alarmCount,
            episodeCount = episodeCount,
            isDeleted = isDeleted,
            readAuthority = readAuthority,
        )
    }
}

enum class ReadAuthority {
    FREE,
    MEMBER_SHIP,
    PAYMENT
}

class NovelCommand {
    class Create(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
        val speciesNames: List<String>,
    )

    class Update(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
        val speciesNames: List<String>,
    )
}