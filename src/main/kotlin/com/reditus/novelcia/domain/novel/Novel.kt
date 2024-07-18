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

}

enum class ReadAuthority {
    ALL, MEMBER_SHIP, PAYMENT
}