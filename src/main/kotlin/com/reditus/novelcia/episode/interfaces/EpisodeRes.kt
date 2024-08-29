package com.reditus.novelcia.episode.interfaces

import java.time.LocalDateTime

class EpisodeRes {
    data class Meta(
        val id: Long,
        val title: String,
        val createdAt: LocalDateTime
    )
}