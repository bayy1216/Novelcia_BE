package com.reditus.novelcia.episode.application.port

import com.reditus.novelcia.episode.domain.Episode

interface EpisodeWriter {
    fun save(episode: Episode) : Episode
    fun delete(episodeId: Long)
    fun deleteAllByNovelId(novelId: Long)
}