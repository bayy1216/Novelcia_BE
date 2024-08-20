package com.reditus.novelcia.infrastructure.episode.adapter

import com.reditus.novelcia.domain.episode.EpisodeReadEvent
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.episode.port.EpisodeReadEventProducer
import com.reditus.novelcia.domain.novel.application.NovelViewWriteBackManager
import com.reditus.novelcia.infrastructure.episode.EpisodeRepository
import com.reditus.novelcia.infrastructure.episode.EpisodeViewRepository
import com.reditus.novelcia.infrastructure.novel.NovelRepository
import com.reditus.novelcia.infrastructure.user.UserRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


@Repository
class RdbEpisodeReadEventProducer(
    private val episodeViewRepository: EpisodeViewRepository,
    private val episodeRepository: EpisodeRepository,
    private val novelRepository: NovelRepository,
    private val userRepository: UserRepository,
    private val novelViewWriteBackManager: NovelViewWriteBackManager,
) : EpisodeReadEventProducer {

    /**
     * 에피소드 조회 이벤트 발행
     * - 현재는 RDB에 저장하는 방식으로 구현
     * - 조회로직에서는 read-only로 처리되기 때문에 새로운 트랜잭션으로 처리
     * - 유실되어도 큰 문제가 없는 데이터이기 때문에 비동기로 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    override fun publish(event: EpisodeReadEvent) {
        val episodeView = EpisodeView(
            episode = episodeRepository.getReferenceById(event.episodeId),
            novel = novelRepository.getReferenceById(event.novelId),
            user = userRepository.getReferenceById(event.userId),
        )
        episodeViewRepository.save(episodeView)
        novelViewWriteBackManager.save(episodeView)
    }
}