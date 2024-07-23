package com.reditus.novelcia.domain.episode

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.user.port.UserReader
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EpisodeQueryService(
    private val userReader: UserReader,
    private val episodeReader: EpisodeReader,
    private val publisher : ApplicationEventPublisher,
) {

    /**
     * 에피소드 페이징 조회
     * 현재는 비정규화가 안되있어서 Projection처리를 Reader에서 처리한다.
     */
    @Transactional(readOnly = true)
    fun getEpisodeModelsByOffsetPaging(
        userId: LoginUserId,
        novelId: Long,
        pageRequest: PageRequest,
        sort: EpisodePagingSort,
    ): List<EpisodeModel.Meta> {
        val episodeMetaModels = episodeReader.getEpisodeModelsByOffsetPaging(
            userId.value,
            novelId,
            pageRequest,
            sort,
        )
        return episodeMetaModels
    }

    @Transactional(readOnly = true)
    fun getEpisodeDetail(
        episodeId: Long,
        userId: LoginUserId,
    ) : EpisodeModel.Main {
        val episode = episodeReader.getByIdWithNovel(episodeId)
        val user = userReader.getById(userId.value)
        if(!episode.canRead(user)){
            throw IllegalAccessException("해당 에피소드를 읽을 권한이 없습니다.")
        }
        val event = EpisodeReadEvent(novelId = episode.novel.id, episodeId = episode.id, userId = userId.value)
        publisher.publishEvent(event)

        return EpisodeModel.Main.from(episode)
    }
}