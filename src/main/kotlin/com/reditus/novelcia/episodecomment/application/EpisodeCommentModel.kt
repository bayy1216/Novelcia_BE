package com.reditus.novelcia.episodecomment.application

import com.reditus.novelcia.episodecomment.domain.EpisodeComment
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.user.application.UserModel

class EpisodeCommentModel {
    class Main(
        val id: Long,
        val episodeId: Long,
        val user: UserModel,
        val content: String,
        val parentCommentId: Long?,
    ){
        companion object{
            fun from(comment: EpisodeComment): TxScope.()-> Main {
                return {
                    Main(
                        id = comment.id,
                        episodeId = comment.episode.id,
                        user = UserModel.from(comment.user)(),
                        content = comment.content,
                        parentCommentId = comment.parent?.id,
                    )
                }
            }
        }
    }
}