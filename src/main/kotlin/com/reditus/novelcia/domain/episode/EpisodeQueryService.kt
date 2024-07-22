package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EpisodeQueryService(
    private val userReader: EpisodeReader,
    private val episodeReader: EpisodeReader,
) {

    /**
     * 에피소드 페이징 조회
     * 현재는 비정규화가 안되있어서 Projection처리를 Reader에서 처리한다.
     */
    @Transactional(readOnly = true)
    fun getEpisodeModelsByOffsetPaging(
        novelId: Long,
        pageRequest: PageRequest,
        sort: EpisodePagingSort,
    ): List<EpisodeModel.Meta> {
        return episodeReader.getEpisodeModelsByOffsetPaging(novelId, pageRequest, sort)
    }

    @Transactional(readOnly = true)
    fun getEpisodeDetail(
        episodeId: Long,
        userId: LoginUserId,
    ) : EpisodeModel.Main {
        val episode = episodeReader.getById(episodeId)

        return EpisodeModel.Main.from(episode)
    }
}