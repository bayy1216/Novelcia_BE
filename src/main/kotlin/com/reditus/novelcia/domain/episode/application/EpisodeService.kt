package com.reditus.novelcia.domain.episode.application

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeCommand
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.episode.port.EpisodeWriter
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service

@Service
class EpisodeService(
    private val novelReader: NovelReader,
    private val episodeReader: EpisodeReader,
    private val episodeWriter: EpisodeWriter,
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
        val lastEpisodeNumber = episodeReader.getLastEpisodeNumberByNovelId(novelId) ?: 0
        val episode = Episode.create(novel, lastEpisodeNumber + 1, command)
        episodeWriter.save(episode)
        return@transactional episode.id
    }

    fun updateEpisode() {

    }

    fun deleteEpisode() {

    }
}