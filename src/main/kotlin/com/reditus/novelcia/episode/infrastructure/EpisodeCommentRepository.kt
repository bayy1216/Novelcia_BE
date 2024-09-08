package com.reditus.novelcia.episode.infrastructure

import com.reditus.novelcia.episode.domain.EpisodeComment
import org.springframework.data.jpa.repository.JpaRepository

interface EpisodeCommentRepository: JpaRepository<EpisodeComment, Long> {
}