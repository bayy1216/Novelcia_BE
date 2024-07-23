package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.novel.ReadAuthority
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
            fun from(episode: Episode): EpisodeModel.Main = EpisodeModel.Main(
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