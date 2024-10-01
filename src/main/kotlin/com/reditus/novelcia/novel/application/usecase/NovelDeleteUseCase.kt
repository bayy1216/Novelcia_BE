package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.episode.application.port.EpisodeWriter
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.application.port.NovelWriter
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Component

@Component
class NovelDeleteUseCase(
    private val novelWriter: NovelWriter,
    private val episodeWriter: EpisodeWriter,
) {
    /**
     * 소설과 소설의 모든 에피소드를 삭제합니다.
     * soft delete를 통해 삭제마크를 남깁니다.
     * - episode와 관련된 comment는 삭제하지 않습니다.
     */
    operator fun invoke(novel: Novel) = transactional{
        novelWriter.delete(novel.id)
        episodeWriter.deleteAllByNovelId(novel.id)
    }
}