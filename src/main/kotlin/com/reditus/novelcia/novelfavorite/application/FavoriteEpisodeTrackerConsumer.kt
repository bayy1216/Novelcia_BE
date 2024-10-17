package com.reditus.novelcia.novelfavorite.application

import com.reditus.novelcia.episode.domain.EpisodeReadEvent
import com.reditus.novelcia.global.util.executeAsync
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novelfavorite.infrastructure.NovelFavoriteRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FavoriteEpisodeTrackerConsumer(
    private val novelFavoriteRepository: NovelFavoriteRepository,
) {
    /**
     * Episode 조회시, 선호작이 존재한다면, 마지막 조회한 에피소드 번호를 업데이트한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun consume(event: EpisodeReadEvent) = executeAsync {
        transactional {
            val favorite = novelFavoriteRepository.findByUserIdAndNovelId(event.userId, event.novelId)
            if (favorite != null) {
                favorite.lastViewedEpisodeNumber = event.episodeNumber
            }
        }

    }
}