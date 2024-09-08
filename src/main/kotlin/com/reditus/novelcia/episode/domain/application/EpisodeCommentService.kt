package com.reditus.novelcia.episode.domain.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.EpisodeCommentCommand
import com.reditus.novelcia.episode.domain.port.EpisodeCommentReader
import com.reditus.novelcia.episode.domain.port.EpisodeCommentWriter
import com.reditus.novelcia.episode.domain.port.EpisodeReader
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.user.domain.port.UserReader
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class EpisodeCommentService(
    private val userReader: UserReader,
    private val episodeReader: EpisodeReader,
    private val episodeCommentReader: EpisodeCommentReader,
    private val episodeCommentWriter: EpisodeCommentWriter,
) {
    fun getEpisodeCommentModelsByPaging(
        episodeId: Long,
        pageRequest: PageRequest,
    ): List<EpisodeCommentModel.Main> = readOnly {
        val comments = episodeCommentReader.findByEpisodeIdPagingOrderByPath(episodeId, pageRequest)
        return@readOnly comments.map { EpisodeCommentModel.Main.from(it)() }
    }

    fun createEpisodeComment(
        episodeId: Long,
        command: EpisodeCommentCommand.Create,
        userId: LoginUserId,
    ): Long = transactional {
        val user = userReader.getReferenceById(userId.value)
        val episode = episodeReader.getById(episodeId)
        val parentComment: EpisodeComment? = command.parentCommentId?.let { episodeCommentReader.getById(it) }
        val comment = EpisodeComment.create(
            episode = episode,
            user = user,
            command = command,
            parentComment = parentComment,
        )
        episodeCommentWriter.save(comment)
        return@transactional comment.id
    }

    fun updateEpisodeComment(
        commentId: Long, command: EpisodeCommentCommand.Update,
        loginUserId: LoginUserId,
    ) = transactional {
        val comment = episodeCommentReader.getById(commentId)
        if (comment.user.id != loginUserId.value) {
            throw NoPermissionException("해당 댓글을 수정할 권한이 없습니다.")
        }
        comment.update(command)
    }

    fun deleteEpisodeComment(
        commentId: Long,
        loginUserId: LoginUserId,
    ) = transactional {
        val comment = episodeCommentReader.getById(commentId)
        if (comment.user.id != loginUserId.value) {
            throw NoPermissionException("해당 댓글을 삭제할 권한이 없습니다.")
        }
        episodeCommentWriter.delete(comment)
    }
}