package com.reditus.novelcia.infrastructure.episode.adapter

import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.port.EpisodeWriter
import com.reditus.novelcia.infrastructure.episode.EpisodeRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class EpisodeWriterImpl(
    private val episodeRepository: EpisodeRepository
) : EpisodeWriter {
    override fun save(episode: Episode): Episode {
        return episodeRepository.save(episode)
    }

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