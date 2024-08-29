package com.reditus.novelcia.episode.interfaces

import com.reditus.novelcia.episode.domain.EpisodeCommand
import com.reditus.novelcia.novel.domain.ReadAuthority

class EpisodeReq {
    data class Create(
        val title: String,
        val content: String,
        val authorComment: String,
        val readAuthority: ReadAuthority,
    ){
        fun toCommand() = EpisodeCommand.Create(
            title = title,
            content = content,
            authorComment = authorComment,
            readAuthority = readAuthority
        )
    }

    data class Patch(
        val title: String?,
        val content: String?,
        val authorComment: String?,
        val readAuthority: ReadAuthority?,
    ){
        fun toCommand() = EpisodeCommand.Patch(
            title = title,
            content = content,
            authorComment = authorComment,
            readAuthority = readAuthority
        )
    }
}