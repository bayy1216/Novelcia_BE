package com.reditus.novelcia.novel.domain

import com.reditus.novelcia.common.domain.BaseModifiableEntity
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.novelmeta.domain.Tag
import jakarta.persistence.Entity

import com.reditus.novelcia.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

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
    var episodeCount: Int,

    @Column(nullable = false)
    var isDeleted: Boolean,

    @Enumerated(EnumType.STRING)
    var readAuthority: ReadAuthority,

    @Column(columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    var novelMeta: NovelMeta,

    @Version
    var version: Int = 0, // 변경감지로 인한 벌크연산 lost update 방어
) : BaseModifiableEntity() {

    val authorId: Long
        get() = author.id

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
        updateTags: List<Tag>,
        updateSpeciesList: List<Species>,
    ) {
        title = command.title
        description = command.description
        thumbnailImageUrl = command.thumbnailImageUrl
        novelMeta = NovelMeta.from(updateTags, updateSpeciesList)
    }



    fun addEpisodeCount() {
        episodeCount++
    }

    fun subtractEpisodeCount() {
        episodeCount--
    }

    companion object {
        fun create(
            author: User,
            command: NovelCommand.Create,
            tags: List<Tag>,
            speciesList: List<Species>,
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
                novelMeta = NovelMeta.from(tags, speciesList),
            )
        }

        fun fixture(
            id: Long = 0L,
            author: User = User.fixture(),
            title: String = "title",
            description: String = "description",
            thumbnailImageUrl: String? = null,
            viewCount: Long = 0,
            likeCount: Long = 0,
            favoriteCount: Long = 0,
            alarmCount: Long = 0,
            episodeCount: Int = 0,
            isDeleted: Boolean = false,
            readAuthority: ReadAuthority = ReadAuthority.FREE,
            metaData: NovelMeta = NovelMeta.empty(),
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
            novelMeta = metaData,
        )
    }
}

data class NovelMeta(
    val tags: List<TagData>,
    val speciesList: List<SpeciesData>,
){
    data class TagData(
        val name: String,
        val colorHexCode: String,
    ){
        companion object {
            fun from(tag: Tag) = TagData(tag.name, tag.colorHexCode)
        }
    }

    data class SpeciesData(
        val id: Long,
        val name: String,
        val colorHexCode: String,
    ){
        companion object {
            fun from(species: Species) = SpeciesData(species.id, species.name, species.colorHexCode)
        }
    }

    companion object {
        fun from(tags: List<Tag>, speciesList: List<Species>): NovelMeta {
            return NovelMeta(
                tags = tags.map { TagData.from(it) },
                speciesList = speciesList.map { SpeciesData.from(it) }
            )
        }
        fun empty() = NovelMeta(emptyList(), emptyList())
    }
}

inline fun <T> Novel.authAsAuthor(userId: Long, message: String = "해당 소설을 수정할 권한이 없습니다.", action: Novel.() -> T): T {
    if (!this.isAuthor(userId)) {
        throw NoPermissionException(message)
    }
    return this.action()
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
    ){
        init{
            if(tagNames.toSet().size != tagNames.size){
                throw IllegalArgumentException("태그 이름이 중복되었습니다.")
            }

            if(speciesNames.toSet().size != speciesNames.size){
                throw IllegalArgumentException("분류 이름이 중복되었습니다.")
            }
        }
    }

    class Update(
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val tagNames: List<String>,
        val speciesNames: List<String>,
    ){
        init{
            if(tagNames.toSet().size != tagNames.size){
                throw IllegalArgumentException("태그 이름이 중복되었습니다.")
            }

            if(speciesNames.toSet().size != speciesNames.size){
                throw IllegalArgumentException("분류 이름이 중복되었습니다.")
            }
        }
    }
}