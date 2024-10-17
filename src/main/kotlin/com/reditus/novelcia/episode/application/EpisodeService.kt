package com.reditus.novelcia.episode.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeCommand
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.infrastructure.EpisodeLikeRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeQueryRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class EpisodeService(
    private val novelRepository: NovelRepository,
    private val episodeQueryRepository: EpisodeQueryRepository,
    private val episodeRepository: EpisodeRepository,
    private val userRepository: UserRepository,
    private val episodeLikeRepository: EpisodeLikeRepository,
) {

    /**
     * 1. Novel의 작가인지 권한 체크
     * 2. novel의 마지막 에피소드 번호를 조회하여 새로운 에피소드 생성시 번호를 부여
     * 3. 에피소드 생성
     */
    fun createEpisode(
        userId: LoginUserId,
        novelId: Long,
        command: EpisodeCommand.Create,
    ): Long = transactional {
        val novel = novelRepository.findByIdOrThrow(novelId)
        if (!novel.isAuthor(userId.value)) {
            throw NoPermissionException("해당 소설에 에피소드를 작성할 권한이 없습니다.")
        }
        val lastEpisodeNumber: Int? = episodeQueryRepository.findLastEpisodeNumberByNovelId(novelId)
        val episodeNumber = if (lastEpisodeNumber == null) {
            Episode.INITIAL_EPISODE_NUMBER
        } else {
            lastEpisodeNumber + 1
        }

        val episode = Episode.create(novel, episodeNumber, command)
        episodeRepository.save(episode)
        novel.addEpisodeCount()
        return@transactional episode.id
    }

    fun patchEpisode(
        userId: LoginUserId,
        episodeId: Long,
        command: EpisodeCommand.Patch,
    ) = transactional {
        val episode = episodeQueryRepository.getByIdWithNovel(episodeId)
        if (!episode.canEdit(userId.value)) {
            throw NoPermissionException("해당 에피소드를 수정할 권한이 없습니다.")
        }
        episode.patch(command)
    }

    fun deleteEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        val episode = episodeQueryRepository.getByIdWithNovel(episodeId)
        if (!episode.canEdit(userId.value)) {
            throw NoPermissionException("해당 에피소드를 삭제할 권한이 없습니다.")
        }
        episodeRepository.delete(episode)
        episode.novel.subtractEpisodeCount()
    }

    fun likeEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        val episode = episodeRepository.getReferenceById(episodeId)
        val user = userRepository.getReferenceById(userId.value)
        val episodeLike = EpisodeLike.create(episode, user)
        episodeLikeRepository.save(episodeLike)
    }

    fun unlikeEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        episodeLikeRepository.deleteByEpisodeIdAndUserId(episodeId, userId.value)
    }
}