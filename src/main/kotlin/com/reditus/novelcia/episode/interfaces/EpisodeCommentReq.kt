package com.reditus.novelcia.episode.interfaces

import com.reditus.novelcia.episode.domain.EpisodeCommentCommand

class EpisodeCommentReq {
    data class Create(
        val parentCommentId: Long?,
        val content: String,
    ){
        fun toCommand() = EpisodeCommentCommand.Create(
            parentCommentId = parentCommentId,
            content = content,
        )
    }

    data class Update(
        val content: String,
    ){
        fun toCommand() = EpisodeCommentCommand.Update(
            content = content,
        )
    }
}