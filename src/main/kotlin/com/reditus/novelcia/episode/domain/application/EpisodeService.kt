package com.reditus.novelcia.episode.domain.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeCommand
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.domain.port.EpisodeLikeWriter
import com.reditus.novelcia.episode.domain.port.EpisodeReader
import com.reditus.novelcia.episode.domain.port.EpisodeWriter
import com.reditus.novelcia.novel.domain.port.NovelReader
import com.reditus.novelcia.user.domain.port.UserReader
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service

@Service
class EpisodeService(
    private val novelReader: NovelReader,
    private val episodeReader: EpisodeReader,
    private val episodeWriter: EpisodeWriter,
    private val userReader: UserReader,
    private val episodeLikeWriter: EpisodeLikeWriter,
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
        val novel = novelReader.getNovelById(novelId)
        if (!novel.isAuthor(userId.value)) {
            throw NoPermissionException("해당 소설에 에피소드를 작성할 권한이 없습니다.")
        }
        val lastEpisodeNumber: Int? = episodeReader.findLastEpisodeNumberByNovelId(novelId)
        val episodeNumber = if (lastEpisodeNumber == null) {
            Episode.INITIAL_EPISODE_NUMBER
        } else {
            lastEpisodeNumber + 1
        }

        val episode = Episode.create(novel, episodeNumber, command)
        episodeWriter.save(episode)
        novel.addEpisodeCount()
        return@transactional episode.id
    }

    fun patchEpisode(
        userId: LoginUserId,
        episodeId: Long,
        command: EpisodeCommand.Patch,
    ) = transactional {
        val episode = episodeReader.getByIdWithNovel(episodeId)
        if (!episode.canEdit(userId.value)) {
            throw NoPermissionException("해당 에피소드를 수정할 권한이 없습니다.")
        }
        episode.patch(command)
    }

    fun deleteEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        val episode = episodeReader.getByIdWithNovel(episodeId)
        if (!episode.canEdit(userId.value)) {
            throw NoPermissionException("해당 에피소드를 삭제할 권한이 없습니다.")
        }
        episodeWriter.delete(episode.id)
        episode.novel.subtractEpisodeCount()
    }

    fun likeEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        val episode = episodeReader.getReferenceById(episodeId)
        val user = userReader.getReferenceById(userId.value)
        val episodeLike = EpisodeLike.create(episode, user)
        episodeLikeWriter.save(episodeLike)
    }

    fun unlikeEpisode(
        userId: LoginUserId,
        episodeId: Long,
    ) = transactional {
        episodeLikeWriter.deleteByEpisodeIdAndUserId(episodeId, userId.value)
    }
}