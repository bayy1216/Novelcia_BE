package com.reditus.novelcia.interfaces.episode

import com.reditus.novelcia.domain.episode.EpisodeCommand
import com.reditus.novelcia.domain.novel.ReadAuthority

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
}