package com.reditus.novelcia.episode.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.episode.application.model.EpisodeModel
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.domain.EpisodeReadEvent
import com.reditus.novelcia.episode.application.port.EpisodeLikeReader
import com.reditus.novelcia.episode.application.port.EpisodePagingSort
import com.reditus.novelcia.episode.application.port.EpisodeReader
import com.reditus.novelcia.novelfavorite.domain.NovelFavorite
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.novelfavorite.infrastructure.NovelFavoriteRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class EpisodeQueryService(
    private val userRepository: UserRepository,
    private val episodeReader: EpisodeReader,
    private val episodeLikeReader: EpisodeLikeReader,
    private val novelFavoriteRepository: NovelFavoriteRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    /**
     * 에피소드 페이징 조회
     * 현재는 비정규화가 안되있어서 Projection처리를 Reader에서 처리한다.
     */

    fun getEpisodeModelsByOffsetPaging(
        userId: LoginUserId,
        novelId: Long,
        pageRequest: PageRequest,
        sort: EpisodePagingSort,
    ): List<EpisodeModel.Meta> = readOnly {
        val episodeMetaModels = episodeReader.getEpisodeModelsByOffsetPaging(
            userId.value,
            novelId,
            pageRequest,
            sort,
        )
        return@readOnly episodeMetaModels
    }


    fun getEpisodeDetail(
        novelId: Long,
        episodeNumber: Int,
        userId: LoginUserId,
    ): EpisodeModel.Detail = readOnly {
        val episode = episodeReader.getByEpisodeNumberAndNovelIdWithNovel(episodeNumber=episodeNumber, novelId=novelId)
        val user = userRepository.findByIdOrThrow(userId.value)
        if (!episode.canRead(user)) {
            throw IllegalAccessException("해당 에피소드를 읽을 권한이 없습니다.")
        }
        val event = EpisodeReadEvent(
            novelId = episode.novel.id,
            episodeId = episode.id,
            userId = userId.value,
            episodeNumber = episodeNumber,
        )
        applicationEventPublisher.publishEvent(event)


        val (episodeLike, novelFavorite, maxEpisodeNumber) = getMetaDateFromEpisode(novelId, episode.id, userId.value)

        return@readOnly  EpisodeModel.Detail.from(
            episode,
            episodeLike != null,
            novelFavorite != null,
            maxEpisodeNumber,
        )()
    }

    private fun getMetaDateFromEpisode(novelId:Long, episodeId: Long, userId: Long)= readOnly {
        val episodeLike = episodeLikeReader.findByEpisodeIdAndUserId(episodeId = episodeId, userId = userId)
        val novelFavorite: NovelFavorite? =
            novelFavoriteRepository.findByUserIdAndNovelId(userId = userId, novelId = novelId)
        val maxEpisodeNumber = episodeReader.findLastEpisodeNumberByNovelId(novelId = novelId)!!
        return@readOnly EpisodeMetaData(episodeLike, novelFavorite, maxEpisodeNumber)
    }
    private data class EpisodeMetaData(
        val episodeLike: EpisodeLike?,
        val novelFavorite: NovelFavorite?,
        val maxEpisodeNumber: Int
    )

}