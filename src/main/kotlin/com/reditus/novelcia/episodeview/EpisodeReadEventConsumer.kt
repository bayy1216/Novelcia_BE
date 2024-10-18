package com.reditus.novelcia.episodeview

import com.reditus.novelcia.episode.domain.EpisodeReadEvent
import com.reditus.novelcia.global.util.executeAsync
import com.reditus.novelcia.novel.application.NovelViewWriteBackManager
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EpisodeReadEventConsumer(
    private val novelWriteBackManager: NovelViewWriteBackManager,
    private val episodeViewRepository: EpisodeViewRepository,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(event: EpisodeReadEvent) = executeAsync {
        val episodeView = EpisodeView(
            episodeId = event.episodeId,
            novelId = event.novelId,
            userId = event.userId
        )

        episodeViewRepository.save(episodeView)


        novelWriteBackManager.save(event)
    }
}