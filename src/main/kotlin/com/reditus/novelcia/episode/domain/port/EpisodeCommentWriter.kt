package com.reditus.novelcia.episode.domain.port

import com.reditus.novelcia.episode.domain.EpisodeComment

interface EpisodeCommentWriter {
    fun delete(comment: EpisodeComment)
}