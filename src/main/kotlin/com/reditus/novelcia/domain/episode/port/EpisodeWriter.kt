package com.reditus.novelcia.domain.episode.port

interface EpisodeWriter {
    fun delete(episodeId: Long)
    fun deleteAllByNovelId(novelId: Long)
}