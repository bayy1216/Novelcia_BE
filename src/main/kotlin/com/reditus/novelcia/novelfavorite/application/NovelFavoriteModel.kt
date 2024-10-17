package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.novelmeta.application.SpeciesModel
import com.reditus.novelcia.novelmeta.application.TagModel
import com.reditus.novelcia.user.application.UserModel

class NovelFavoriteModel {
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