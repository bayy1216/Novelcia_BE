package com.reditus.novelcia.episode.domain.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.episode.domain.EpisodeCommentCommand
import com.reditus.novelcia.episode.domain.port.EpisodeCommentReader
import com.reditus.novelcia.episode.domain.port.EpisodeCommentWriter
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class EpisodeCommentService(
    private val episodeCommentReader: EpisodeCommentReader,
    private val episodeCommentWriter: EpisodeCommentWriter,
) {
    fun getEpisodeComments(episodeId: Long, toPageRequest: PageRequest): List<EpisodeCommentModel.Main> {
        TODO("Not yet implemented")
    }
    fun createEpisodeComment(
        episodeId: Long,
        command: EpisodeCommentCommand.Create,
        userId: LoginUserId,
    ): Long {
        TODO("Not yet implemented")
    }

    fun updateEpisodeComment(
        commentId: Long, command: EpisodeCommentCommand.Update,
        loginUserId: LoginUserId,
    ) {

    }

    fun deleteEpisodeComment(
        commentId: Long,
        loginUserId: LoginUserId,
    ) {

    }
}