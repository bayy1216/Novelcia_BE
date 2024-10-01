package com.reditus.novelcia.episode.application

import com.reditus.novelcia.episode.domain.EpisodeReadEvent
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.episode.application.port.EpisodeReader
import com.reditus.novelcia.episode.application.port.EpisodeViewWriter
import com.reditus.novelcia.global.util.executeAsync
import com.reditus.novelcia.global.util.newTransaction
import com.reditus.novelcia.novel.application.NovelViewWriteBackManager
import com.reditus.novelcia.novel.application.port.NovelReader
import com.reditus.novelcia.user.application.port.UserReader
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EpisodeReadEventConsumer(
    private val episodeReader: EpisodeReader,
    private val novelReader: NovelReader,
    private val userReader: UserReader,
    private val episodeViewWriter: EpisodeViewWriter,
    private val novelWriteBackManager: NovelViewWriteBackManager,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(event: EpisodeReadEvent) = executeAsync {
        val episodeView = EpisodeView(
            episode = episodeReader.getReferenceById(event.episodeId),
            novel = novelReader.getReferenceById(event.novelId),
            user = userReader.getReferenceById(event.userId),
        )
        newTransaction {
            episodeViewWriter.save(episodeView)
        }

        novelWriteBackManager.save(episodeView)
    }
}