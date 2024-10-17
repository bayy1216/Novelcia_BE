package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import org.springframework.stereotype.Component

@Component
class NovelDeleteUseCase(
    private val novelRepository: NovelRepository,
    private val episodeRepository: EpisodeRepository,
) {
    /**
     * 소설과 소설의 모든 에피소드를 삭제합니다.
     * soft delete를 통해 삭제마크를 남깁니다.
     * - episode와 관련된 comment는 삭제하지 않습니다.
     */
    operator fun invoke(novel: Novel) = transactional{
        novel.isDeleted = true //TODO: soft delete
        episodeRepository.softDeleteAllByNovelId(novel.id)
    }
}