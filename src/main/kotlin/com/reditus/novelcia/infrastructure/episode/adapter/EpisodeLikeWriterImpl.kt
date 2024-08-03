package com.reditus.novelcia.infrastructure.episode.adapter

import com.reditus.novelcia.domain.episode.EpisodeLike
import com.reditus.novelcia.domain.episode.port.EpisodeLikeWriter
import com.reditus.novelcia.infrastructure.episode.EpisodeLikeRepository
import org.springframework.stereotype.Repository

@Repository
class EpisodeLikeWriterImpl(
    private val episodeLikeRepository: EpisodeLikeRepository,
) : EpisodeLikeWriter {
    override fun save(episodeLike: EpisodeLike): EpisodeLike {
        return episodeLikeRepository.save(episodeLike)
    }

    override fun deleteByEpisodeIdAndUserId(episodeId: Long, userId: Long) {
        val affected = episodeLikeRepository.deleteByEpisodeIdAndUserId(episodeId, userId)
        if (affected == 0) {
            throw IllegalArgumentException("해당 에피소드 좋아요 정보가 존재하지 않습니다.")
        }
    }
}