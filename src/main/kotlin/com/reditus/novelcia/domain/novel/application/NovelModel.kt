package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.user.UserModel
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
        val episodeCount: Long,
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
}