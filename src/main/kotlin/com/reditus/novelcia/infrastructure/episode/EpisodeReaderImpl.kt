package com.reditus.novelcia.infrastructure.episode

import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeModel
import com.reditus.novelcia.domain.episode.QEpisode
import com.reditus.novelcia.domain.episode.QEpisode.episode
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class EpisodeReaderImpl(
    private val episodeRepository: EpisodeRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeReader {
    override fun getEpisodeModelsByOffsetPaging(
        novelId: Long,
        pageRequest: PageRequest,
        sort: EpisodePagingSort
    ): List<EpisodeModel.Meta> {
        return emptyList()
    }

    override fun getById(episodeId: Long): Episode {
        return episodeRepository.findByIdOrThrow(episodeId)
    }

    override fun getByIdWithNovel(episodeId: Long): Episode {
        return episodeRepository.findByIdWithNovel(episodeId) ?: throw NoSuchElementException("Episode not found")
    }

    override fun getLastEpisodeNumberByNovelId(novelId: Long): Int? {
        val query = jpaQueryFactory.selectFrom(episode)
            .where(episode.novel.id.eq(novelId))
            .orderBy(episode.episodeNumber.desc())
            .fetchFirst()
        return query?.episodeNumber
    }
}