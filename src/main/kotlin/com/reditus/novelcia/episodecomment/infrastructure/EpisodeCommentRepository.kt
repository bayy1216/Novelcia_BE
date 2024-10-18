package com.reditus.novelcia.episodecomment.infrastructure

import com.reditus.novelcia.episodecomment.domain.EpisodeComment
import org.springframework.data.jpa.repository.JpaRepository

interface EpisodeCommentRepository: JpaRepository<EpisodeComment, Long> {
}