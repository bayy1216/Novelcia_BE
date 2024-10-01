package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.EpisodeComment

interface EpisodeCommentWriter {
    fun save(comment: EpisodeComment): EpisodeComment
    fun delete(comment: EpisodeComment)
}