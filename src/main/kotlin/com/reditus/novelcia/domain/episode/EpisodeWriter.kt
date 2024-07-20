package com.reditus.novelcia.domain.episode

interface EpisodeWriter {
    fun delete(episodeId: Long)
    fun deleteAllByNovelId(novelId: Long)
}