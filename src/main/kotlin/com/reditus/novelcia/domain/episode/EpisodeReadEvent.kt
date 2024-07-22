package com.reditus.novelcia.domain.episode

import java.time.LocalDateTime

class EpisodeReadEvent(
    val novelId: Long,
    val episodeId: Long,
    val userId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
}