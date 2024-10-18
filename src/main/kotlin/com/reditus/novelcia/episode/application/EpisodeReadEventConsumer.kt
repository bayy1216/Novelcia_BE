package com.reditus.novelcia.episode.application

import com.reditus.novelcia.episode.domain.EpisodeReadEvent
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeViewRepository
import com.reditus.novelcia.global.util.executeAsync
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.application.NovelViewWriteBackManager
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class EpisodeReadEventConsumer(
    private val episodeRepository: EpisodeRepository,
    private val novelRepository: NovelRepository,
    private val userRepository: UserRepository,
    private val episodeViewRepository: EpisodeViewRepository,
    private val novelWriteBackManager: NovelViewWriteBackManager,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(event: EpisodeReadEvent) = executeAsync {
        val episodeView = EpisodeView(
            episode = episodeRepository.getReferenceById(event.episodeId),
            novel = novelRepository.getReferenceById(event.novelId),
            user = userRepository.getReferenceById(event.userId),
        )
        transactional {
            episodeViewRepository.save(episodeView)
        }

        novelWriteBackManager.save(event)
    }
}