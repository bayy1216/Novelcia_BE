package com.reditus.novelcia.novel.application.model

import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.user.application.UserModel
import com.reditus.novelcia.global.util.TxScope

class NovelModel {
    data class Main(
        val id: Long,
        val author: UserModel,
        val title: String,
        val description: String,
        val thumbnailImageUrl: String?,
        val viewCount: Long,
        val likeCount: Long,
        val favoriteCount: Long,
        val alarmCount: Long,
        val episodeCount: Int,
    ) {
        companion object {
            fun from(novel: Novel): TxScope.() -> Main = {
                Main(
                    id = novel.id,
                    author = UserModel.from(novel.author)(this),
                    title = novel.title,
                    description = novel.description,
                    thumbnailImageUrl = novel.thumbnailImageUrl,
                    viewCount = novel.viewCount,
                    likeCount = novel.likeCount,
                    favoriteCount = novel.favoriteCount,
                    alarmCount = novel.alarmCount,
                    episodeCount = novel.episodeCount,
                )
            }
        }
    }

    data class UserFavorite(
        val id: Long,
        val author: UserModel,
        val title: String,
        val thumbnailImageUrl: String?,
        val viewCount: Long,
        val likeCount: Long,
        val favoriteCount: Long,
        val episodeCount: Int,

        val species: List<SpeciesModel>,
        val tags: List<TagModel>,

        val userLastReadEpisodeNumber: Int,
        val maxEpisodeNumber: Int,
    )

}