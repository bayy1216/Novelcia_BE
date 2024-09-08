package com.reditus.novelcia.episode.domain.application

import com.reditus.novelcia.user.domain.UserModel

class EpisodeCommentModel {
    class Main(
        val id: Long,
        val episodeId: Long,
        val user: UserModel,
        val content: String,
        val parentCommentId: Long?,
    )
}