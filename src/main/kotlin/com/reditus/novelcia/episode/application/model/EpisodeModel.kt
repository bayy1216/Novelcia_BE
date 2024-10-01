package com.reditus.novelcia.episode.application.model

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.novel.domain.ReadAuthority
import com.reditus.novelcia.global.util.TxScope
import java.time.LocalDateTime

class EpisodeModel {
    class Meta(
        val id: Long,
        val title: String,
        val episodeNumber: Int,
        val commentsCount: Int,
        val viewsCount: Int,
        val createdAt: LocalDateTime,
        val readAuthority: ReadAuthority,
        val isRead: Boolean,
    )

    class Main(
        val id: Long,
        val title: String,
        val content: String,
        val episodeNumber: Int,
        val authorComment: String,
        val novelId: Long,
        val createdAt: LocalDateTime,
    ) {
        companion object {
            fun from(episode: Episode): TxScope.() -> Main = {
                Main(
                    id = episode.id,
                    title = episode.title,
                    content = episode.content,
                    episodeNumber = episode.episodeNumber,
                    authorComment = episode.authorComment,
                    novelId = episode.novelId,
                    createdAt = episode.createdAt,
                )
            }
        }
    }

    // TODO 좋아요, 즐겨찾기 여부 추가
    class Detail(
        val id: Long,
        val title: String,
        val content: String,
        val episodeNumber: Int,
        val authorComment: String,
        val createdAt: LocalDateTime,
        //
        val novelId: Long,
        val isLiked: Boolean,
        val isFavoriteNovel: Boolean,
        val maxEpisodeNumber: Int,
    ) {
        companion object {
            fun from(
                episode: Episode,
                isLiked: Boolean,
                isFavorite: Boolean,
                maxEpisodeNumber: Int,
            ): TxScope.() -> Detail = {
                Detail(
                    id = episode.id,
                    title = episode.title,
                    content = episode.content,
                    episodeNumber = episode.episodeNumber,
                    authorComment = episode.authorComment,
                    novelId = episode.novelId,
                    createdAt = episode.createdAt,
                    isLiked = isLiked,
                    isFavoriteNovel = isFavorite,
                    maxEpisodeNumber = maxEpisodeNumber,
                )
            }
        }
    }
}