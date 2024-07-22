package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.Episode

interface EpisodeWriter {
    fun save(episode: Episode) : Episode
    fun delete(episodeId: Long)
    fun deleteAllByNovelId(novelId: Long)
}