package com.reditus.novelcia.interfaces.novel

import com.reditus.novelcia.domain.novel.NovelModel

class NovelRes {
    data class Meta(
        val id: Long,
        val authorNickname: String,
        val title: String,
        val thumbnailImageUrl: String?,
        val favoriteCount: Long,
        val episodeCount: Long,
    ){
        companion object{
            fun from(novel: NovelModel.Main) = Meta(
                id = novel.id,
                authorNickname = novel.author.nickname,
                title = novel.title,
                thumbnailImageUrl = novel.thumbnailImageUrl,
                favoriteCount = novel.favoriteCount,
                episodeCount = novel.episodeCount,
            )
        }
    }
}