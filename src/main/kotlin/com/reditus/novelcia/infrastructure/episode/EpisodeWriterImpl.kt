package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.domain.episode.EpisodeWriter
import org.springframework.stereotype.Repository

@Repository
class EpisodeWriterImpl(
    private val episodeRepository: EpisodeRepository
) : EpisodeWriter {
    override fun delete(episodeId: Long) {
        val affected = episodeRepository.softDeleteById(episodeId)
        if (affected == 0) {
            throw NoSuchElementException("해당 에피소드가 존재하지 않습니다.")
        }
    }

    override fun deleteAllByNovelId(novelId: Long) {
        episodeRepository.softDeleteAllByNovelId(novelId)
    }
}